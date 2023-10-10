package com.machado001.lilol.rotation.presentation

import android.util.Log
import com.machado001.lilol.common.extensions.toDataDragon
import com.machado001.lilol.common.extensions.toRotations
import com.machado001.lilol.common.model.data.DataDragon
import com.machado001.lilol.rotation.Rotation
import com.machado001.lilol.rotation.model.dto.Rotations
import com.machado001.lilol.rotation.model.repository.ChampionsManager
import java.util.Locale

class RotationPresenter(
    private val repository: ChampionsManager,
    private var view: Rotation.View?

) : Rotation.Presenter {

    override suspend fun fetchRotations() {
        view?.showProgress(true)
        try {
            repository.apply {
                val dataDragon: DataDragon = getDataDragon(Locale.getDefault().toString()).toDataDragon()
                val rotations: Rotations = getRotations().toRotations()
                val maxNewPlayerLevel = rotations.maxNewPlayerLevel

                val freeChampions = dataDragon.data.entries.filter { (_, value) ->
                    rotations.freeChampionIds.contains(value.key.toInt())
                }

                val freeChampionsForNewPlayers = dataDragon.data.entries.filter { entry ->
                    rotations.freeChampionIdsForNewPlayers.contains(entry.value.key.toInt())
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