package com.machado001.lilol.rotation.presentation

import com.machado001.lilol.common.extensions.toDataDragon
import com.machado001.lilol.common.model.data.Champion
import com.machado001.lilol.rotation.AllChampions
import com.machado001.lilol.rotation.model.repository.DataDragonRepository
import java.util.Locale
import kotlin.coroutines.coroutineContext
import kotlin.math.min
import kotlinx.coroutines.ensureActive

class AllChampionsPresenter(
    private var view: AllChampions.View?,
    private val repository: DataDragonRepository,
) : AllChampions.Presenter {

    private val pageSize = 12
    private var allChampions: List<Champion> = emptyList()
    private var filteredChampions: List<Champion> = emptyList()
    private var currentQuery: String = ""
    private var selectedRoles: List<String> = emptyList()
    private var currentPage: Int = 0

    override suspend fun loadChampions() {
        view?.showProgress(true)
        try {
            val version = repository.fetchAllGameVersions().first()
            val dataDragon =
                repository.fetchDataDragon(version, Locale.getDefault().toString()).toDataDragon()
            allChampions = dataDragon.data.values.sortedBy { it.name }
            filteredChampions = allChampions
            view?.showRoles(extractRoles(allChampions))
            deliverPage()
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            view?.showErrorMessage(e.message ?: "Unable to load champions")
        } finally {
            view?.showProgress(false)
        }
    }

    override fun onSearchQueryChanged(query: String) {
        currentQuery = query
        currentPage = 0
        applyFilters()
    }

    override fun onRolesChanged(roles: List<CharSequence>) {
        selectedRoles = roles.map { it.toString() }
        currentPage = 0
        applyFilters()
    }

    override fun onNextPage() {
        val totalPages = totalPages()
        if (totalPages == 0) return
        if (currentPage + 1 < totalPages) {
            currentPage++
            deliverPage()
        }
    }

    override fun onPreviousPage() {
        if (currentPage > 0) {
            currentPage--
            deliverPage()
        }
    }

    private fun applyFilters() {
        if (allChampions.isEmpty()) return
        val normalizedQuery = currentQuery.trim().lowercase(Locale.getDefault())
        filteredChampions = allChampions.filter { champion ->
            val matchesRole =
                selectedRoles.isEmpty() || champion.tags.any { tag -> selectedRoles.contains(tag) }
            val matchesQuery = normalizedQuery.isBlank() ||
                champion.name.lowercase(Locale.getDefault()).contains(normalizedQuery) ||
                champion.id.lowercase(Locale.getDefault()).contains(normalizedQuery)

            matchesRole && matchesQuery
        }
        currentPage = 0
        deliverPage()
    }

    private fun deliverPage() {
        val totalPages = totalPages()
        if (filteredChampions.isEmpty()) {
            view?.showChampions(emptyList())
            view?.showPagination(0, 0)
            view?.showEmptyState(true, currentQuery)
            return
        }

        if (currentPage >= totalPages) {
            currentPage = totalPages - 1
        }

        val startIndex = currentPage * pageSize
        val endIndex = min(startIndex + pageSize, filteredChampions.size)
        view?.showChampions(filteredChampions.subList(startIndex, endIndex))
        view?.showPagination(currentPage + 1, totalPages)
        view?.showEmptyState(false)
    }

    private fun totalPages(): Int {
        if (filteredChampions.isEmpty()) return 0
        return ((filteredChampions.size - 1) / pageSize) + 1
    }

    private fun extractRoles(champions: List<Champion>): Set<String> =
        champions.flatMap { it.tags }
            .map { it }
            .distinct()
            .toSet()

    override fun onDestroy() {
        view = null
    }
}
