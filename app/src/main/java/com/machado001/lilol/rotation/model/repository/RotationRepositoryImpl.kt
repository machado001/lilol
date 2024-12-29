package com.machado001.lilol.rotation.model.repository

import android.util.Log
import com.machado001.lilol.common.extensions.TAG
import com.machado001.lilol.common.extensions.toRotations
import com.machado001.lilol.rotation.model.background.BackgroundTaskManager
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
    private val localDataSource: RotationLocalDataSource,
    private val backgroundTaskManager: BackgroundTaskManager
) : RotationRepository {

    private val cacheMutex = Mutex()
    private var rotationsDto: RotationsDto? = null

    override suspend fun fetchRemoteRotations(refresh: Boolean): RotationsDto {
        if (rotationsDto == null || refresh) {
            withContext(ioDispatcher) {
                try {
                    val networkResult = apiDataSource.fetchRotations()
                    cacheMutex.withLock {
                        rotationsDto = networkResult
                        localDataSource.setRotation(rotationsDto!!.toRotations())
                        backgroundTaskManager.scheduleTask()
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

}