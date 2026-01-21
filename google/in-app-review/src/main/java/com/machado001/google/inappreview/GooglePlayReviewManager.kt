package com.machado001.google.inappreview

import android.app.Activity
import android.content.Context
import androidx.core.content.edit
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import kotlinx.coroutines.suspendCancellableCoroutine
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class GooglePlayReviewManager(
    private val reviewManager: ReviewManager,
) {

    suspend fun maybeLaunchReview(activity: Activity) {
        val prefs =
            activity.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val launchCount = prefs.getInt(KEY_LAUNCH_COUNT, 0)
        if (launchCount == 0) {
            prefs.edit { putInt(KEY_LAUNCH_COUNT, launchCount + 1) }
            return
        }
        askForReview(activity)
    }

    suspend fun askForReview(context: Context) {
        val activity = context as? Activity ?: return
        askForReview(activity)
    }

    suspend fun askForReview(activity: Activity) {
        try {
            reviewManager.launchReviewFlow(activity, getReviewInfo())
                .addOnCompleteListener {
                    logcat(LogPriority.INFO) {
                        "launchReviewFlow: completed. is successful? ${it.isSuccessful}"
                    }
                }
        } catch (e: Exception) {
            logcat(LogPriority.ERROR) { e.asLog() }
        }
    }

    private suspend fun getReviewInfo() =
        suspendCancellableCoroutine<ReviewInfo> { continuation ->
            reviewManager.requestReviewFlow()
                .addOnCompleteListener { request ->
                    return@addOnCompleteListener if (request.isSuccessful) {
                        continuation.resume(request.result)
                    } else {
                        val reviewErrorCode = request.exception as? ReviewException
                        if (reviewErrorCode != null) {
                            continuation.resumeWithException(reviewErrorCode)
                        } else {
                            continuation.resumeWithException(
                                IllegalStateException("Review request failed.", request.exception)
                            )
                        }
                    }
                }
        }

    private companion object {
        private const val PREFS_NAME = "review_prefs"
        private const val KEY_LAUNCH_COUNT = "review_launch_count"
    }
}
