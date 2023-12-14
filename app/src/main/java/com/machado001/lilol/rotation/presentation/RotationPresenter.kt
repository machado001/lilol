package com.machado001.lilol.rotation.presentation

import android.util.Log
import com.machado001.lilol.common.extensions.toDataDragon
import com.machado001.lilol.common.extensions.toRotations
import com.machado001.lilol.common.model.data.Champion
import com.machado001.lilol.common.model.data.DataDragon
import com.machado001.lilol.rotation.Rotation
import com.machado001.lilol.rotation.model.dto.Rotations
import com.machado001.lilol.rotation.model.repository.ChampionsManager
import java.util.Locale

class RotationPresenter(
    private val repository: ChampionsManager,
    private var view: Rotation.View?,

    ) : Rotation.Presenter {

    private suspend fun getDataDragon(): DataDragon =
        repository.getDataDragon(Locale.getDefault().toString()).toDataDragon()

    override suspend fun getRotations(): Rotations =
        repository.getRotations().toRotations()


    override suspend fun getFreeChampionsForNewPlayers() =
        getDataDragon().data.entries.filter { entry ->
            getRotations().freeChampionIdsForNewPlayers.contains(entry.value.key.toInt())
        }

    override suspend fun getFreeChampions(): List<Map.Entry<String, Champion>> {
        val freeChampions = getDataDragon().data.entries.filter { (_, value) ->
            getRotations().freeChampionIds.contains(value.key.toInt())
        }
        return freeChampions
    }

    override suspend fun fetchRotations() {
        view?.showProgress(true)
        try {
            val maxNewPlayerLevel = getRotations().maxNewPlayerLevel
            val freeChampions = getFreeChampions()
            val freeChampionsForNewPlayers = getFreeChampionsForNewPlayers()

            view?.showSuccess(
                freeChampions,
                freeChampionsForNewPlayers,
                maxNewPlayerLevel,
            )

        } catch (e: Exception) {
            view?.showFailureMessage()
            Log.e("i caraio", e.toString())
        } finally {
            view?.showProgress(false)
        }
    }

    override fun onDestroy() {
        view = null
    }
}