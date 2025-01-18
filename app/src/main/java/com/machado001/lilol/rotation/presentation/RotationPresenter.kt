package com.machado001.lilol.rotation.presentation

import android.util.Log
import com.machado001.lilol.common.extensions.TAG
import com.machado001.lilol.common.extensions.toDataDragon
import com.machado001.lilol.common.extensions.toRotations
import com.machado001.lilol.common.model.data.Champion
import com.machado001.lilol.common.model.data.DataDragon
import com.machado001.lilol.rotation.Rotation
import com.machado001.lilol.rotation.model.dto.Rotations
import com.machado001.lilol.rotation.model.repository.ChampionsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.security.InvalidParameterException
import java.util.Locale
import kotlin.coroutines.coroutineContext

class RotationPresenter(
    private val repository: ChampionsManager,
    private var view: Rotation.View?,

    ) : Rotation.Presenter {

    private suspend fun getDataDragon(): DataDragon =
        repository.getDataDragon(Locale.getDefault().toString()).toDataDragon()

    override suspend fun getRotations(): Rotations =
        repository.getRotations().toRotations()


    override suspend fun getFreeChampionsForNewPlayers(rotations: Rotations): List<Map.Entry<String, Champion>> {
        return getDataDragon().data.entries.filter { entry ->
            rotations.freeChampionIdsForNewPlayers.contains(entry.value.key.toInt())
        }
    }

    override suspend fun getFreeChampions(rotations: Rotations): List<Map.Entry<String, Champion>> {
        val freeChampions = getDataDragon().data.entries.filter { (_, value) ->
            rotations.freeChampionIds.contains(value.key.toInt())
        }
        return freeChampions
    }

    override suspend fun displayRotations() {
        view?.showProgress(true)
        try {
            coroutineScope {
                val rotations = async(Dispatchers.IO) { getRotations() }
                val freeChampions = async(Dispatchers.IO) { getFreeChampions(rotations.await()) }
                val freeChampionsForNewPlayers =
                    async(Dispatchers.IO) { getFreeChampionsForNewPlayers(rotations.await()) }

                if (rotations.await().freeChampionIds.isEmpty()) throw InvalidParameterException()

                withContext(Dispatchers.Main.immediate) {
                    view?.showSuccess(
                        freeChampionsMap = freeChampions.await(),
                        freeChampionForNewPlayersMap = freeChampionsForNewPlayers.await(),
                        level = rotations.await().maxNewPlayerLevel,
                    )
                }
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            view?.showFailureMessage()
            e.printStackTrace()
            Log.e(TAG, e.toString())
        } finally {
            view?.showProgress(false)
        }
    }

    override fun onDestroy() {
        view = null
    }
}