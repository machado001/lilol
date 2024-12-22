package com.machado001.lilol.rotation.model.background

import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager

class RotationBackgroundTaskManager(
    private val workManager: WorkManager,
    private val workRequest: PeriodicWorkRequest.Builder
) : BackgroundTaskManager {

    override fun scheduleTask(data: Data) {
        workManager.enqueueUniquePeriodicWork(
            uniqueWorkName = ROTATION_WORK_NAME,
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
            request = workRequest
                .setInputData(data)
                .build()
        )
    }

    companion object {
        const val ROTATION_WORK_NAME = "RotationWork"
    }
}