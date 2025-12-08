package com.machado001.lilol.rotation.model.repository

import android.util.Log
import com.machado001.lilol.common.extensions.TAG
import com.machado001.lilol.common.extensions.toRotations
import com.machado001.lilol.rotation.model.dto.RotationsDto
import com.machado001.lilol.rotation.model.local.RotationLocalDataSource
import com.machado001.lilol.rotation.model.network.RotationNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import retrofit2.HttpException


class RotationRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val apiDataSource: RotationNetworkDataSource,
    private val localDataSource: RotationLocalDataSource
) : RotationRepository {

    private val cacheMutex = Mutex()
    private var rotationsDto: RotationsDto? = null
    private val signatureMutex = Mutex()

    override suspend fun fetchRemoteRotations(refresh: Boolean): RotationsDto {
        if (rotationsDto == null || refresh) {
            withContext(ioDispatcher) {
                try {
                    val networkResult = apiDataSource.fetchRotations()
                    cacheMutex.withLock {
                        rotationsDto = networkResult
                        localDataSource.setRotation(rotationsDto!!.toRotations())
                    }
                } catch (e: HttpException) {
                    Log.d(TAG, "fetchRemoteRotations: $e")
                } catch (e: Exception) {
                    ensureActive()
                    Log.d(TAG, "fetchRemoteRotations: $e")
                }
            }
        }
        return rotationsDto ?: RotationsDto(emptyList(), emptyList(), 0)
    }

    override val localRotations: Flow<String> = localDataSource.rotation

    override suspend fun getLocalSignature(): String? {
        val local = localDataSource.getLocalRotation()
        val ids = local?.freeChampionIds.orEmpty()
        if (ids.isEmpty()) return null
        return signatureMutex.withLock {
            ids.sorted().joinToString(",")
        }
    }

    override suspend fun updateRotationFromPayload(payload: Map<*, *>) {
        val dto = mapPayloadToRotationsDto(payload) ?: return
        cacheMutex.withLock {
            rotationsDto = dto
        }
        localDataSource.setRotation(dto.toRotations())
    }

    private fun mapPayloadToRotationsDto(payload: Map<*, *>): RotationsDto? {
        val freeChampionIds = (payload["freeChampionIds"] as? List<*>)?.mapNotNull {
            when (it) {
                is Number -> it.toInt()
                is String -> it.toIntOrNull()
                else -> null
            }
        } ?: emptyList()

        val freeChampionIdsForNewPlayers =
            (payload["freeChampionIdsForNewPlayers"] as? List<*>)?.mapNotNull {
                when (it) {
                    is Number -> it.toInt()
                    is String -> it.toIntOrNull()
                    else -> null
                }
            } ?: emptyList()

        val maxNewPlayerLevel = (payload["maxNewPlayerLevel"] as? Number)?.toInt() ?: 0

        return RotationsDto(
            freeChampionIds = freeChampionIds,
            freeChampionIdsForNewPlayers = freeChampionIdsForNewPlayers,
            maxNewPlayerLevel = maxNewPlayerLevel
        )
    }

}
