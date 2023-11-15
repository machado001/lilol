package com.machado001.lilol.rotation.presentation

import com.machado001.lilol.common.extensions.toDataDragon
import com.machado001.lilol.rotation.AllChampions
import com.machado001.lilol.rotation.model.repository.DataDragonRepository
import java.util.Locale

class AllChampionsPresenter(
    private var view: AllChampions.View?,
    private val repository: DataDragonRepository,

    ) : AllChampions.Presenter {
    override suspend fun getAllChampions() {
        view?.showProgress(true)
        try {
            val latestVersion = repository.fetchAllGameVersions().first()
            val dataDragon =
                repository.fetchDataDragon(latestVersion, Locale.getDefault().toString())
                    .toDataDragon()
            val allChampions = dataDragon.data.values.toList()
            view?.showChampions(allChampions)
        } catch (e: Exception) {
            e.message?.let { view?.showErrorMessage(it) }
        } finally {
            view?.showProgress(false)
        }
    }

    override suspend fun filterChampions(roles: List<CharSequence>) {
        val latestVersion = repository.fetchAllGameVersions().first()
        val dataDragon =
            repository.fetchDataDragon(latestVersion, Locale.getDefault().toString()).toDataDragon()
        val allChampions = dataDragon.data.values.toList()

        val filteredChampions = dataDragon.data.values.filter {
//            roles.any { role -> it.tags.contains(role) }
            roles.any { role -> it.tags.first() == role }
        }

        if (roles.isEmpty()) {
            view?.showChampions(allChampions)
        } else {
            view?.showChampions(filteredChampions)
        }
    }

    override suspend fun getAllRoles(): Set<String> {
        val latestVersion = repository.fetchAllGameVersions().first()
        val dataDragon =
            repository.fetchDataDragon(latestVersion, Locale.getDefault().toString()).toDataDragon()
        val allRoles = dataDragon.data.values.map {
            it.tags
        }.flatten().toSet()

        return allRoles
    }

    override fun onDestroy() {
        view = null
    }
}