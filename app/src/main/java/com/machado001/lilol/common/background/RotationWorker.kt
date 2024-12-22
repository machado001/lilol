package com.machado001.lilol.common.background

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.machado001.lilol.Application
import com.machado001.lilol.MyNotification
import com.machado001.lilol.common.extensions.TAG
import com.machado001.lilol.common.extensions.toRotations
import com.machado001.lilol.rotation.model.dto.Rotations
import com.machado001.lilol.rotation.model.local.RotationLocalDataSource
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

class RotationWorker(
    private val appContext: Context,
    workerParams: WorkerParameters,
) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            (appContext as Application).container.run {
                val localSource = rotationLocal
                val networkSource = rotationApi.fetchRotations().toRotations()
                compareLocalAndNetworkData(networkSource, localSource)
            }
            Result.success()
        } catch (e: Exception) {
            Log.d(TAG, "fail: $e")
            currentCoroutineContext().ensureActive()
            Result.failure()
        }
    }

    private suspend fun compareLocalAndNetworkData(
        networkDataSource: Rotations,
        localDataSource: RotationLocalDataSource
    ) {
        localDataSource.rotation.collect { local ->
            if (networkDataSource.toString() != local) {
                MyNotification(appContext).showNotification()
            }
        }
    }
}