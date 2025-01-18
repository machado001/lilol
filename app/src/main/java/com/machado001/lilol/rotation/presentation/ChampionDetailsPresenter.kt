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
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlin.coroutines.coroutineContext


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
                val championDetailsDto = async {
                    championDetailsRepository.getSpecificChampion(
                        version,
                        lang,
                        championName
                    ).toChampionDetails()
                }

                val relatedChampions = async {
                    championDetailsRepository.fetchDataDragon(version, lang)
                        .toDataDragon().data
                        .entries
                        .filter { (_, championData) ->
                            championData.tags.any { role ->
                                yield()
                                championDetailsDto.await().tags.contains(role)
                            }
                        }
                        .filter { (_, championData) ->
                            yield()
                            championData.tags.first() == championDetailsDto.await().tags.first() || championData.tags.first() == championDetailsDto.await().tags.last()
                        }
                        .filter { (_, championData) ->//condition to exclude the same champion to appear in the related list
                            yield()
                            championData.key != championDetailsDto.await().key
                        }
                        .shuffled()
                        .subList(0, 16)
                }


                withContext(Dispatchers.Main.immediate) {
                    val spellList =
                        championDetailsDto.await().spells.mapIndexed { index, spell ->
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
                        setupChampionDetails(championDetailsDto.await())
                        setupRecyclerView(relatedChampions.await())
                    }
                }
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
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
