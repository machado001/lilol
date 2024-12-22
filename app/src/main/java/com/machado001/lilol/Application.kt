package com.machado001.lilol

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.machado001.lilol.common.di.AppContainer
import com.machado001.lilol.common.di.Container
import com.machado001.lilol.common.extensions.TAG
import com.machado001.lilol.rotation.model.background.RotationWorkerFactory
import com.machado001.lilol.rotation.model.repository.ChampionsManager
import com.machado001.lilol.rotation.model.repository.DataDragonRepository
import com.machado001.lilol.rotation.model.repository.RotationRepository
import com.machado001.lilol.rotation.model.repository.SettingsRepository

/**
 * Application to apply Manual DI.
 */
class Application : Application(), Configuration.Provider {

    lateinit var container: Container

    override fun onCreate() {
        super.onCreate()
        val appContainer = AppContainer(this)
        container = appContainer
        WorkManager.initialize(this, workManagerConfiguration)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(RotationWorkerFactory(container))
            .build()
}
