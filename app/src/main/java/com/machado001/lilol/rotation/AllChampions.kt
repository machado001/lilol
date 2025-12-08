package com.machado001.lilol.rotation

import com.machado001.lilol.common.base.BasePresenter
import com.machado001.lilol.common.base.BaseView
import com.machado001.lilol.common.model.data.Champion

interface AllChampions {
    interface Presenter : BasePresenter {
        suspend fun loadChampions()
        fun onSearchQueryChanged(query: String)
        fun onRolesChanged(roles: List<CharSequence>)
        fun onNextPage()
        fun onPreviousPage()
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
        fun showRoles(roles: Set<String>)
        fun showPagination(currentPage: Int, totalPages: Int)
        fun showEmptyState(show: Boolean, query: String = "")
    }
}
