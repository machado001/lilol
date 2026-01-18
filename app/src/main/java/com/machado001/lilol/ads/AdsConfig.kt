package com.machado001.lilol.ads

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

/**
 * Configuration for Ads.
 * Controls whether ads should be shown based on purchase history.
 */
object AdsConfig {
    private const val PREF_SHOW_AD_ON_RESTART = "show_ad_on_restart"

    /**
     * Checks if ads should be shown.
     * Returns true because remove-ads is currently disabled.
     */
    fun shouldShowAds(context: Context): Boolean {
        return true
    }

    /**
     * Sets a flag to show an interstitial ad when the activity restarts.
     * Uses commit() to ensure synchronous write to disk before restart.
     */
    fun setPendingAdOnRestart(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit(commit = true) { putBoolean(PREF_SHOW_AD_ON_RESTART, true) }
    }

    /**
     * Checks if there is a pending ad to show on restart, and consumes the flag.
     * Uses commit() to ensure synchronous write to disk.
     */
    fun checkAndConsumePendingAdOnRestart(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val shouldShow = prefs.getBoolean(PREF_SHOW_AD_ON_RESTART, false)
        if (shouldShow) {
            prefs.edit(commit = true) { remove(PREF_SHOW_AD_ON_RESTART) }
        }
        return shouldShow
    }
}
