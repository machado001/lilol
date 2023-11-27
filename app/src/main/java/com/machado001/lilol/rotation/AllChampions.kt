package com.machado001.lilol.rotation

import com.machado001.lilol.common.base.BasePresenter
import com.machado001.lilol.common.base.BaseView
import com.machado001.lilol.common.model.data.Champion

interface AllChampions {
    interface Presenter : BasePresenter {
        suspend fun getAllChampions()
        suspend fun filterChampions(roles: List<CharSequence>)
        suspend fun getAllRoles(): Set<String>
    }

    interface View : BaseView<Presenter> {
        fun goToChampionDetails(
            championId: String,
            championName: String,
            championVersion: String,
        )

        fun showErrorMessage(msg: String)
        fun showProgress(enabled: Boolean)
        fun showChampions(allChampions: List<Champion>)
        fun setupRecyclerViewAllChampions()
    }
}