package com.machado001.lilol.rotation.presentation

import android.util.Log
import com.machado001.lilol.common.extensions.TAG
import com.machado001.lilol.common.extensions.toChampionDetails
import com.machado001.lilol.common.extensions.toDataDragon
import com.machado001.lilol.common.model.data.Champion
import com.machado001.lilol.common.view.SpellListItem
import com.machado001.lilol.rotation.ChampionDetails
import com.machado001.lilol.rotation.model.repository.DataDragonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext


class ChampionDetailsPresenter(
    private val championDetailsRepository: DataDragonRepository,
    private var view: ChampionDetails.View?,
) : ChampionDetails.Presenter {
    override suspend fun getChampionDetails(
        version: String,
        lang: String,
        championName: String,
    ) {
        view?.showProgress(true)

        try {
            coroutineScope {
                val championDetailsDeferred = async {
                    championDetailsRepository.getSpecificChampion(
                        version,
                        lang,
                        championName
                    ).toChampionDetails()
                }

                val allChampionsDeferred = async {
                    championDetailsRepository.fetchDataDragon(version, lang)
                        .toDataDragon()
                }

                val championDetails = championDetailsDeferred.await()
                val allChampionsMap = allChampionsDeferred.await().data

                val relatedChampions = withContext(Dispatchers.Default) {
                     allChampionsMap.entries
                        .asSequence()
                        .filter { it.value.key != championDetails.key } // Exclude self
                        .filter { entry ->
                            // logic: Candidate's primary role must be one of the Target's roles.
                            // This implies they share at least one tag (intersection), specifically the candidate's main one.
                            val candidate = entry.value
                            candidate.tags.firstOrNull() in championDetails.tags
                        }
                        .toList()
                        .shuffled()
                        .take(16)
                }

                withContext(Dispatchers.Main.immediate) {
                    val spellList =
                        championDetails.spells.mapIndexed { index, spell ->
                            SpellListItem(
                                id = spell.id,
                                keyboardKey = when (index) {
                                    0 -> 'Q'
                                    1 -> 'W'
                                    2 -> 'E'
                                    3 -> 'R'
                                    else -> 'K'
                                },
                                spellImageUri = spell.image,
                            )
                        }
                    view?.apply {
                        showSpellList(spellList)
                        setupChampionDetails(championDetails)
                        showSkinsList(championDetails.skins, championDetails.id)
                        setupRecyclerView(relatedChampions)
                    }
                }
            }
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            e.message?.let { Log.e(TAG, it) }
            view?.showErrorMessage()
        } finally {
            view?.showProgress(false)
        }

    }

    override fun getRelatedChampions(relatedChampions: List<Champion>) {
    }

    override fun onDestroy() {
        view = null
    }
}
