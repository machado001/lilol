package com.machado001.lilol.rotation.presentation

import com.machado001.lilol.common.extensions.toDataDragon
import com.machado001.lilol.rotation.AllChampions
import com.machado001.lilol.rotation.model.repository.DataDragonRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.yield
import java.util.Locale
import kotlin.coroutines.coroutineContext

class AllChampionsPresenter(
    private var view: AllChampions.View?,
    private val repository: DataDragonRepository,

    ) : AllChampions.Presenter {

    override suspend fun getAllChampions() {
        view?.showProgress(true)
        try {
            coroutineScope {
                val latestVersion = async {
                    repository.fetchAllGameVersions().first()
                }

                yield()
                val dataDragon = async {
                    repository.fetchDataDragon(
                        latestVersion.await(),
                        Locale.getDefault().toString()
                    ).toDataDragon()
                }

                yield()
                val allChampions = dataDragon.await().data.values.toList()
                view?.showChampions(allChampions)
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
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