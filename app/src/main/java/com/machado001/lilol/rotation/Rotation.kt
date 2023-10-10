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

        fun goToChampionDetailsScreen(championId: String, championName: String)
        fun showFailureMessage()
        fun showSuccess(
            freeChampionsMap: List<Map.Entry<String, Champion>>,
            freeChampionForNewPlayersMap: List<Map.Entry<String, Champion>>,
            level: Int,
        )
    }
}