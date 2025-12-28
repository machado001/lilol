package com.machado001.lilol.common.view

import android.view.View

private const val DEFAULT_FADE_OUT_MS = 160L

fun View.fadeOutAndGone(duration: Long = DEFAULT_FADE_OUT_MS) {
    if (visibility != View.VISIBLE) return
    animate().cancel()
    animate()
        .alpha(0f)
        .setDuration(duration)
        .withEndAction {
            visibility = View.GONE
            alpha = 1f
        }
        .start()
}
