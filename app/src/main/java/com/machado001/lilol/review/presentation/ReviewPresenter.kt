package com.machado001.lilol.review.presentation

import com.machado001.google.inappreview.GooglePlayReviewManager
import com.machado001.lilol.review.Review

class ReviewPresenter(
    private var view: Review.View?,
    private val reviewManager: GooglePlayReviewManager,
) : Review.Presenter {

    override suspend fun maybePromptForReview() {
        val activity = view?.getReviewActivity() ?: return
        reviewManager.maybeLaunchReview(activity)
    }

    override fun onDestroy() {
        view = null
    }
}
