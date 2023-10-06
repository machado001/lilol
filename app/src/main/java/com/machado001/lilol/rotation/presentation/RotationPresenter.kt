package com.machado001.lilol.rotation.presentation

import android.util.Log
import com.machado001.lilol.common.extensions.toDataDragon
import com.machado001.lilol.common.model.data.Champion
import com.machado001.lilol.common.model.data.DataDragon
import com.machado001.lilol.rotation.Rotation
import com.machado001.lilol.rotation.model.dto.Rotations
import com.machado001.lilol.rotation.model.dto.toRotations
import com.machado001.lilol.rotation.model.repository.ChampionsManager

class RotationPresenter(
    private val repository: ChampionsManager,
    private var view: Rotation.View?

) : Rotation.Presenter {

    override suspend fun fetchRotations() {
        view?.showProgress(true)
        try {
            repository.apply {
                val allLangs = getSupportedLanguages()

                val dataDragon: DataDragon = getDataDragon(allLangs.first()).toDataDragon()
                val rotations: Rotations = getRotations().toRotations()
                val maxNewPlayerLevel = rotations.maxNewPlayerLevel

                val freeChampions = dataDragon.data.values.filter {
                    rotations.freeChampionIds.contains(it.id.toInt())
                }.map {
                    Champion(
                        id = it.id,
                        name = it.name,
                        image = getImageByPath(getCurrentVersion(), it.image)
                    )
                }

                val freeChampionsForNewPlayers = dataDragon.data.values.filter {
                    rotations.freeChampionIdsForNewPlayers.contains(it.id.toInt())
                }.map {
                    Champion(
                        id = it.id,
                        name = it.name,
                        image = getImageByPath(getCurrentVersion(), it.image)
                    )
                }

                view?.showSuccess(
                    freeChampions,
                    freeChampionsForNewPlayers,
                    maxNewPlayerLevel,
                )
            }

        } catch (e: Exception) {
            view?.showFailureMessage()
            Log.e("i caraio", e.toString())
        } finally {
            view?.showProgress(false)
        }
    }

    override fun getImageByPath(version: String, path: String) =
        "https://ddragon.leagueoflegends.com/cdn/$version/img/champion/$path"

    override fun onDestroy() {
        view = null
    }
}