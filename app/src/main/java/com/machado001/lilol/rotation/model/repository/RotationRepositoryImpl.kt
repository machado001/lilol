package com.machado001.lilol.rotation.model.repository

import android.util.Log
import androidx.work.Data
import com.machado001.lilol.common.extensions.TAG
import com.machado001.lilol.common.extensions.toRotations
import com.machado001.lilol.rotation.model.background.BackgroundTaskManager
import com.machado001.lilol.rotation.model.dto.RotationsDto
import com.machado001.lilol.rotation.model.local.RotationLocalDataSource
import com.machado001.lilol.rotation.model.network.RotationNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext


class RotationRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val apiDataSource: RotationNetworkDataSource,
    private val localDataSource: RotationLocalDataSource,
    private val backgroundTaskManager: BackgroundTaskManager
) : RotationRepository {

    private val cacheMutex = Mutex()
    private var rotationsDto: RotationsDto? = null

    override suspend fun fetchRotations(refresh: Boolean): RotationsDto {
        if (rotationsDto == null || refresh) {
            withContext(ioDispatcher) {
                val networkResult = apiDataSource.fetchRotations()
                cacheMutex.withLock {
                    rotationsDto = networkResult
                    localDataSource.setRotation(networkResult.toRotations())
                }
                try {
                    val data = Data.Builder()
                        .putString("local", localDataSource.rotation.toString())
                        .build()

                    backgroundTaskManager.scheduleTask(data)
                } catch (e: Exception) {
                    ensureActive()
                    Log.d(TAG, "fetchRotations: $e")
                }
            }
        }
        return rotationsDto!!
    }

}