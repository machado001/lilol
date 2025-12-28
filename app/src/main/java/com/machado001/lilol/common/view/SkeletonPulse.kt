package com.machado001.lilol.common.view

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import com.machado001.lilol.R

private const val SKELETON_MIN_ALPHA = 0.45f
private const val SKELETON_MAX_ALPHA = 1f
private const val SKELETON_DURATION_MS = 900L

fun View.startSkeletonPulse() {
    if (getTag(R.id.skeleton_pulse_animator) != null) return
    val animator = ObjectAnimator.ofFloat(this, View.ALPHA, SKELETON_MIN_ALPHA, SKELETON_MAX_ALPHA).apply {
        duration = SKELETON_DURATION_MS
        repeatMode = ObjectAnimator.REVERSE
        repeatCount = ObjectAnimator.INFINITE
        interpolator = LinearInterpolator()
    }
    setTag(R.id.skeleton_pulse_animator, animator)
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) = Unit

        override fun onViewDetachedFromWindow(v: View) {
            v.stopSkeletonPulse()
            v.removeOnAttachStateChangeListener(this)
        }
    })
    animator.start()
}

fun View.stopSkeletonPulse() {
    val animator = getTag(R.id.skeleton_pulse_animator) as? ObjectAnimator ?: return
    animator.cancel()
    setTag(R.id.skeleton_pulse_animator, null)
    alpha = 1f
}
