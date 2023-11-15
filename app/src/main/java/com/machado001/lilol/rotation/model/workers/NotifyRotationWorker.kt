package com.machado001.lilol.rotation.model.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.machado001.lilol.MyNotification
import com.machado001.lilol.rotation.model.repository.RotationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotifyRotationWorker(
    private val ctx: Context,
    params: WorkerParameters,
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {

        return withContext(Dispatchers.IO) {

            return@withContext try {
                MyNotification(ctx).showNotification()
                Log.i("TAGG", "FUCK ")
                Result.success()
            } catch (e: Exception) {
                e.message?.let { Log.i(Result.failure().toString(), it) }
                Result.failure()
            }
        }

    }
}