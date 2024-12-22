package com.machado001.lilol.common.background

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.machado001.lilol.MyNotification
import com.machado001.lilol.rotation.model.local.RotationLocalDataSource
import com.machado001.lilol.rotation.model.network.RotationNetworkDataSource
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

class RotationWorker(
    private val appContext: Context,
    workerParams: WorkerParameters,
    private val networkDataSource: RotationNetworkDataSource,
    private val localDataSource: RotationLocalDataSource,
) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {

            val network = networkDataSource.fetchRotations().toString()

            localDataSource.rotation.collect { local ->
                if (network != local) {
                    MyNotification(appContext).showNotification()
                }
            }

            Result.success()
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            Result.failure()
        }
    }
}