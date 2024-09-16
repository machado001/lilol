package com.machado001.lilol.rotation.presentation

import android.util.Log
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.util.Locale
import kotlin.coroutines.coroutineContext
import kotlin.system.measureTimeMillis

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

    override suspend fun displayRotations() {
        view?.showProgress(true)
        try {
            coroutineScope {
                val rotations = async(Dispatchers.IO) { getRotations() }
                val freeChampions = async(Dispatchers.IO) { getFreeChampions() }
                val freeChampionsForNewPlayers =
                    async(Dispatchers.IO) { getFreeChampionsForNewPlayers() }

                yield()

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
            Log.e("i caraio", e.toString())
        } finally {
            view?.showProgress(false)
        }
    }

    override fun onDestroy() {
        view = null
    }
}