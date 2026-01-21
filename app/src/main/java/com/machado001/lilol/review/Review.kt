package com.machado001.lilol.review

import android.app.Activity
import com.machado001.lilol.common.base.BasePresenter
import com.machado001.lilol.common.base.BaseView

interface Review {

    interface Presenter : BasePresenter {
        suspend fun maybePromptForReview()
    }

    interface View : BaseView<Presenter> {
        fun getReviewActivity(): Activity
    }
}
