package com.machado001.lilol.rotation.presentation

import com.machado001.lilol.rotation.Home
import com.machado001.lilol.rotation.model.repository.DataDragonRepository

class HomePresenter(
    private val repository: DataDragonRepository,
    private var view: Home.View?,

    ) : Home.Presenter {

    override fun onNavigateToFreeWeekScreen() {
        view?.goToFreeWeekScreen()
    }

    override fun onNavigateToAllChampionsScreen() {
        view?.goToAllChampionsScreen()
    }

    override suspend fun getActualGameVersion() {
        val version = repository.fetchAllGameVersions().first()
        view?.showGameVersion(version)
    }

    override fun onDestroy() {
        view = null
    }
}