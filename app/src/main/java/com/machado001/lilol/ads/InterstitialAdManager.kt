package com.machado001.lilol.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.machado001.lilol.BuildConfig
import com.machado001.lilol.common.extensions.TAG
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import logcat.logcat
import kotlin.coroutines.resume

/**
 * Manages Interstitial Ads.
 * Uses Kotlin Coroutines for safe and modern async handling.
 */
object InterstitialAdManager {
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    private var isShowing = false

    interface Listener {
        fun onAdShown() {}
        fun onAdDismissed() {}
        fun onAdFailedToShow() {}
    }

    /**
     * Preloads an interstitial ad using Coroutines.
     * Call this from a CoroutineScope (e.g., lifecycleScope.launch).
     */
    suspend fun preload(context: Context) {
        if (!AdsConfig.shouldShowAds(context)) return
        if (interstitialAd != null || isLoading) return

        isLoading = true
        val adRequest = AdRequest.Builder().build()

        try {
            interstitialAd = suspendCancellableCoroutine { continuation ->
                InterstitialAd.load(
                    context,
                    BuildConfig.ADMOB_INTERSTITIAL_AD_UNIT_ID,
                    adRequest,
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(ad: InterstitialAd) {
                            Log.d(TAG, "Interstitial ad loaded.")
                            if (continuation.isActive) continuation.resume(ad)
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Log.d(TAG, "Interstitial ad failed to load: ${adError.message}")
                            if (continuation.isActive) continuation.resume(null)
                        }
                    }
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception loading interstitial: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    /**
     * Shows the interstitial ad if available.
     * @param activity The current activity.
     * @param onFinished Callback invoked when the ad is closed, failed to show, or not available.
     */
    fun showIfAvailable(
        activity: Activity,
        listener: Listener? = null,
        onFinished: () -> Unit
    ) {
        if (!AdsConfig.shouldShowAds(activity)) {
            onFinished()
            return
        }

        if (isShowing) {
            logcat{"Interstitial already showing, skipping."}
            onFinished()
            return
        }

        if (interstitialAd != null) {
            isShowing = true
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad dismissed fullscreen content.")
                    interstitialAd = null
                    isShowing = false
                    // Preload the next ad safely using lifecycle scope
                    (activity as? LifecycleOwner)?.lifecycleScope?.launch {
                        preload(activity)
                    }
                    listener?.onAdDismissed()
                    onFinished()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(TAG, "Ad failed to show fullscreen content.")
                    interstitialAd = null
                    isShowing = false
                    listener?.onAdFailedToShow()
                    onFinished()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad showed fullscreen content.")
                    interstitialAd = null
                    listener?.onAdShown()
                }
            }
            interstitialAd?.setImmersiveMode(true)
            interstitialAd?.show(activity)
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.")
            // Try to load one for next time
            (activity as? LifecycleOwner)?.lifecycleScope?.launch {
                preload(activity)
            }
            onFinished()
        }
    }
}
