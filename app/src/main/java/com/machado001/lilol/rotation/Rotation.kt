package com.machado001.lilol.rotation

import com.machado001.lilol.common.ListChampionPair
import com.machado001.lilol.common.base.BasePresenter
import com.machado001.lilol.common.base.BaseView
import com.machado001.lilol.common.model.data.Champion
import com.machado001.lilol.rotation.model.dto.Rotations

interface Rotation {

    interface Presenter : BasePresenter {
        suspend fun displayRotations()
        suspend fun getFreeChampions(rotations:Rotations): List<Map.Entry<String, Champion>>
        suspend fun getFreeChampionsForNewPlayers(rotations:Rotations): List<Map.Entry<String, Champion>>

        suspend fun getRotations(): Rotations
    }

    interface View : BaseView<Presenter> {
        fun showProgress(enabled: Boolean)
        fun goToChampionDetailsScreen(
            championId: String,
            championName: String,
            championVersion: String,
        )

        fun showFailureMessage()
        fun showSuccess(
            freeChampionsMap: ListChampionPair,
            freeChampionForNewPlayersMap: ListChampionPair,
            level: Int,
        )
    }
}
