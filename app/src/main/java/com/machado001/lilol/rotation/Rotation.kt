package com.machado001.lilol.rotation

import com.machado001.lilol.common.base.BasePresenter
import com.machado001.lilol.rotation.model.Champion

interface Rotation {

    interface Presenter : BasePresenter {
        suspend fun fetchRotations()
    }


    interface View {
        fun showProgress(enabled: Boolean)
        fun showFailureMessage()
        fun showSuccess(
            freeChampionIds: List<Champion>,
            freeChampionIdsForNewPlayers: List<Champion>,
            level: Int,
            date: String
        )
    }
}