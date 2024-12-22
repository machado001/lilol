package com.machado001.lilol.rotation.model.repository

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.machado001.lilol.BuildConfig
import com.machado001.lilol.common.background.RotationWorker
import com.machado001.lilol.common.extensions.TAG
import com.machado001.lilol.common.extensions.toRotations
import com.machado001.lilol.rotation.model.dto.RotationsDto
import com.machado001.lilol.rotation.model.local.RotationLocalDataSource
import com.machado001.lilol.rotation.model.network.RotationNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit


class RotationRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val apiDataSource: RotationNetworkDataSource,
    private val localDataSource: RotationLocalDataSource,
    private val context: Context,
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
            }
            implementTask()
        }
        return rotationsDto!!
    }

    private fun implementTask() {

        val worker = PeriodicWorkRequestBuilder<RotationWorker>(
            1, TimeUnit.DAYS,
            15, TimeUnit.MINUTES,
        )
            .addTag("Rotation")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "RotationWork", ExistingPeriodicWorkPolicy.KEEP, worker
        )

//        val worker = OneTimeWorkRequestBuilder<RotationWorker>()
//            .build()
//
//        WorkManager.getInstance(context).enqueue(worker)
    }
}