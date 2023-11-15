package com.machado001.lilol.rotation

import com.machado001.lilol.common.base.BasePresenter
import com.machado001.lilol.common.base.BaseView

interface Home {
    interface Presenter : BasePresenter {
        fun onNavigateToFreeWeekScreen()
        fun onNavigateToAllChampionsScreen()

        suspend fun getActualGameVersion()

    }

    interface View : BaseView<Presenter> {

        fun goToFreeWeekScreen()
        fun goToAllChampionsScreen()
        fun setupOnClickListeners()

        fun showGameVersion(version: String)
    }
}