package com.machado001.lilol.rotation.presentation

import android.util.Log
import com.machado001.lilol.common.extensions.toChampionDetails
import com.machado001.lilol.common.extensions.toDataDragon
import com.machado001.lilol.common.model.data.Champion
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

            view?.setupChampionDetails(championDetails)
            view?.setupRecyclerView(relatedChampions)
        } catch (e: Exception) {
            e.message?.let { Log.e("KKK L", it) }
            view?.showErrorMessage()
        } finally {
            view?.showProgress(false)
        }
    }

    override fun getRelatedChampions(relatedChampions: List<Champion>) {
    }

    override fun getImageByPath(version: String, path: String) =
        "https://ddragon.leagueoflegends.com/cdn/$version/img/champion/$path"

    override fun onDestroy() {
        view = null
    }
}