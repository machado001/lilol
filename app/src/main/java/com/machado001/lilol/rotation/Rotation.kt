package com.machado001.lilol.rotation

import com.machado001.lilol.common.base.BasePresenter
import com.machado001.lilol.common.model.data.Champion

interface Rotation {

    interface Presenter : BasePresenter {
        suspend fun fetchRotations()
        fun getImageByPath(version: String, path: String): String
    }

    interface View {
        fun showProgress(enabled: Boolean)
        fun showFailureMessage()
        fun showSuccess(
            freeChampionIds: List<Champion>,
            freeChampionIdsForNewPlayers: List<Champion>,
            level: Int,
        )
    }
}