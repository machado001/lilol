package com.machado001.lilol.ads

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.core.content.withStyledAttributes
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.machado001.lilol.R

enum class BannerPlacement(val attrValue: Int, @StringRes val unitIdRes: Int) {
    ALL_CHAMPIONS_TOP(0, R.string.ad_unit_banner_all_champions),
    ROTATION_BOTTOM(1, R.string.ad_unit_banner_rotation),
    CHAMPION_DETAILS_BOTTOM(2, R.string.ad_unit_banner_champion_details),
    SETTINGS_BOTTOM(3, R.string.ad_unit_banner_settings);

    companion object {
        fun fromAttrValue(value: Int): BannerPlacement? =
            entries.firstOrNull { it.attrValue == value }
    }
}

class BannerAdView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private var adView = AdView(context)
    private lateinit var adRequest: AdRequest
    private var placement: BannerPlacement? = null
    private var hasRequestedAd = false
    private var adSizeSet = false
    private var lastAdSize: AdSize? = null

    init {
        if (attrs != null) {
            context.withStyledAttributes(attrs, R.styleable.BannerAdView) {
                val placementValue =
                    getInt(R.styleable.BannerAdView_bannerPlacement, -1)
                placement = BannerPlacement.fromAttrValue(placementValue)
            }
        }

        val adSize = calculateAdSize()
        minimumHeight = adSize.getHeightInPixels(context)
        adView.visibility = INVISIBLE
        addView(adView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!hasRequestedAd) {
            loadAd()
        }
    }

    override fun onDetachedFromWindow() {
        adView.destroy()
        super.onDetachedFromWindow()
    }

    private fun loadAd() {
        if (!AdsConfig.shouldShowAds(context)) {
            visibility = GONE
            return
        }

        val resolvedPlacement = placement ?: run {
            visibility = GONE
            return
        }

        val unitId = context.getString(resolvedPlacement.unitIdRes)
        if (unitId.isBlank()) {
            visibility = GONE
            return
        }

        val desiredSize = calculateAdSize()
        minimumHeight = desiredSize.getHeightInPixels(context)
        if (adSizeSet && lastAdSize != desiredSize) {
            replaceAdView()
        }
        if (!adSizeSet) {
            adView.setAdSize(desiredSize)
            adSizeSet = true
            lastAdSize = desiredSize
        }
        adView.adUnitId = unitId
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                adView.visibility = VISIBLE
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                adView.visibility = INVISIBLE
            }
        }
        if (!::adRequest.isInitialized) {
            adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }
        hasRequestedAd = true
    }

    private fun calculateAdSize(): AdSize {
        val displayMetrics = resources.displayMetrics
        val adWidthDp = (displayMetrics.widthPixels / displayMetrics.density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidthDp)
    }

    private fun replaceAdView() {
        adView.destroy()
        removeAllViews()
        adView = AdView(context)
        adView.visibility = INVISIBLE
        addView(adView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        adSizeSet = false
        hasRequestedAd = false
        lastAdSize = null
        if (::adRequest.isInitialized) {
            adRequest = AdRequest.Builder().build()
        }
    }
}
