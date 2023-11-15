package com.machado001.lilol.rotation.model.repository

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.machado001.lilol.rotation.model.dto.RotationsDto
import com.machado001.lilol.rotation.model.network.RotationNetworkDataSource
import com.machado001.lilol.rotation.model.workers.NotifyRotationWorker
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

private const val TAG_FETCH_LATEST_ROTATIONS = "FetchLatestRotationsTaskTag"
private const val FETCH_LATEST_ROTATIONS_TASK = "FetchLatestRotationsTask"


class RotationRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val apiDataSource: RotationNetworkDataSource,
    private val workManager: WorkManager,
) : RotationRepository {

    private val cacheMutex = Mutex()
    private var rotationsDto: RotationsDto? = null
    
    override suspend fun fetchRotations(refresh: Boolean): RotationsDto {
        if (rotationsDto == null || refresh) {
            withContext(ioDispatcher) {

                val networkResult = apiDataSource.fetchRotations()
                cacheMutex.withLock {
                    rotationsDto = networkResult
                }
            }

        }
        return rotationsDto!!
    }

    override suspend fun fetchRotationsPeriodically() {

        withContext(ioDispatcher){
            val networkResult =
                PeriodicWorkRequestBuilder<NotifyRotationWorker>(
                    15, TimeUnit.MINUTES
                )
                    .setConstraints(
                        Constraints(
                            NetworkType.CONNECTED
                        )
                    )
                    .addTag(TAG_FETCH_LATEST_ROTATIONS)

            workManager.enqueueUniquePeriodicWork(
                FETCH_LATEST_ROTATIONS_TASK,
                ExistingPeriodicWorkPolicy.KEEP,
                networkResult.build()
            )
        }

    }
}