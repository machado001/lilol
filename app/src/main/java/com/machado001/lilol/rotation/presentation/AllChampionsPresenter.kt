package com.machado001.lilol.rotation.presentation

import com.machado001.lilol.common.extensions.toDataDragon
import com.machado001.lilol.rotation.AllChampions
import com.machado001.lilol.rotation.model.repository.DataDragonRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import java.util.Locale
import kotlin.coroutines.coroutineContext

class AllChampionsPresenter(
    private var view: AllChampions.View?,
    private val repository: DataDragonRepository,

    ) : AllChampions.Presenter {

    private fun latestVersion() = CoroutineScope(SupervisorJob()).async {
        repository.fetchAllGameVersions().first()
    }

    private fun dataDragon() = CoroutineScope(SupervisorJob()).async {
        repository.fetchDataDragon(
            latestVersion().await(),
            Locale.getDefault().toString()
        ).toDataDragon()
    }

    override suspend fun getAllChampions() {
        view?.showProgress(true)
        try {
            val allChampions = dataDragon().await().data.values.toList()
            view?.showChampions(allChampions)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            e.message?.let { view?.showErrorMessage(it) }
        } finally {
            view?.showProgress(false)
        }
    }

    override suspend fun filterChampions(roles: List<CharSequence>) {
        coroutineScope {
            val allChampions = dataDragon().await().data.values.toList()
            val filteredChampions = dataDragon().await().data.values.filter {
                roles.any { role -> it.tags.first() == role }
            }

            val championToBeDisplayed = if (roles.isEmpty()) allChampions else filteredChampions

            view?.showChampions(championToBeDisplayed)
        }
    }


    override suspend fun getAllRoles(): Set<String> {
        val allRoles = dataDragon().await().data.values.map {
            it.tags
        }.flatten().toSet()
        return allRoles
    }

    override fun onDestroy() {
        view = null
    }
}