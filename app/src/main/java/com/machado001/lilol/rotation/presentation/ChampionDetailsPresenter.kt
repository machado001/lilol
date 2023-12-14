package com.machado001.lilol.rotation.presentation

import android.util.Log
import com.machado001.lilol.common.extensions.toChampionDetails
import com.machado001.lilol.common.extensions.toDataDragon
import com.machado001.lilol.common.model.data.Champion
import com.machado001.lilol.common.view.SpellListItem
import com.machado001.lilol.rotation.ChampionDetails
import com.machado001.lilol.rotation.model.repository.DataDragonRepository


class ChampionDetailsPresenter(
    private val championDetailsRepository: DataDragonRepository,
    private var view: ChampionDetails.View?,
) : ChampionDetails.Presenter {
    override suspend fun getChampionDetails(
        version: String,
        lang: String,
        championName: String, //champion id por hora
    ) {
        view?.showProgress(true)

        try {
            val championDetailsDto =
                championDetailsRepository.getSpecificChampion(
                    version,
                    lang,
                    championName
                )
            val championDetails = championDetailsDto.toChampionDetails()
            val relatedChampions = championDetailsRepository.fetchDataDragon(version, lang)
                .toDataDragon().data.entries.filter { (_, championData) ->
                    championData.tags.any { championDetails.tags.contains(it) }
                }.filter { (_, championData) ->
                    championData.tags.any {
                        championDetails.tags.contains(it)
                    }
                }.filter { (_, championData) -> //condition to fetch related champion
                    championData.tags.first() == championDetails.tags.first() || championData.tags.first() == championDetails.tags.last()
                }
                .filter { (_, championData) ->//condition to exclude the same champion to appear in the related list
                    championData.key != championDetails.key
                }

            val spellList = championDetails.spells.mapIndexed { index, spell ->
                SpellListItem(
                    id = spell.id,
                    keyboardKey = when (index) {
                        0 -> 'Q'
                        1 -> 'W'
                        2 -> 'E'
                        3 -> 'R'
                        else -> {
                            'K'
                        }
                    },
                    spell.image,
                )
            }

            view?.apply {
                showSpellList(spellList)
                setupChampionDetails(championDetails)
                setupRecyclerView(relatedChampions)
            }

            view?.setupChampionDetails(championDetails)
            view?.setupRecyclerView(relatedChampions)
        } catch (e: Exception) {
            e.message?.let { Log.e("KKK", it) }
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
