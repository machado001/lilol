package com.machado001.lilol.rotation.model.background

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.machado001.lilol.common.di.Container

class RotationWorkerFactory(private val container: Container) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return RotationWorker(appContext, workerParameters, container.rotationRepository)
    }
}