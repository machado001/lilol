package com.machado001.lilol.rotation.model.background

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.machado001.lilol.MyNotification
import com.machado001.lilol.common.extensions.toRotations
import com.machado001.lilol.rotation.model.repository.RotationRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import retrofit2.HttpException

class RotationWorker(
    private val appContext: Context,
    workerParams: WorkerParameters,
    private val repository: RotationRepository
) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            with(repository) {
                val networkSource = fetchRemoteRotations().toRotations().toString()
                localRotations.collect { localSource ->
                    compareLocalAndNetworkData(networkSource, localSource)
                }
                Result.success()
            }
        } catch (e: HttpException) {
            return if (e.code() == 429)
                Result.retry() else Result.retry()
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            Result.failure()
        }
    }

    private fun compareLocalAndNetworkData(
        networkDataSource: String,
        localDataSource: String
    ) {
        if (networkDataSource != localDataSource) {
            MyNotification(appContext).showNotification()
        }
    }
}