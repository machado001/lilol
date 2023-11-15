package com.machado001.lilol.rotation

import com.machado001.lilol.common.ListChampionPair
import com.machado001.lilol.common.base.BasePresenter
import com.machado001.lilol.common.base.BaseView
import com.machado001.lilol.common.model.data.Champion

interface ChampionDetails {

    interface Presenter : BasePresenter {
        fun getRelatedChampions(relatedChampions: List<Champion>)
        suspend fun getChampionDetails(
            version: String,
            lang: String,
            championName: String,
        )

        fun getImageByPath(version: String, path: String): String
    }

    interface View : BaseView<Presenter> {
        fun setupRecyclerView(champions: ListChampionPair)
        fun showSuccess(champions: ListChampionPair)
        fun showErrorMessage()
        fun goToMoreDetailsScreen()
        fun setupChampionDetails(championDetail: com.machado001.lilol.common.model.data.ChampionDetails)
        fun showProgress(show: Boolean)

    }
}