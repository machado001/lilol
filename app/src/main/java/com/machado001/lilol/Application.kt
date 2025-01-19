package com.machado001.lilol

import android.app.Application
import android.os.StrictMode
import androidx.work.Configuration
import androidx.work.WorkManager
import com.machado001.lilol.common.di.AppContainer
import com.machado001.lilol.common.di.Container
import com.machado001.lilol.rotation.model.background.RotationWorkerFactory

/**
 * Application to apply Manual DI.
 */
class Application : Application(), Configuration.Provider {

    lateinit var container: Container

    override fun onCreate() {
        super.onCreate()
        configureStrictModePolicy()
        val appContainer = AppContainer(this)
        MyNotification(this).createNotificationChannel()
        container = appContainer
        WorkManager.initialize(this, workManagerConfiguration)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(RotationWorkerFactory(container))
            .build()

    private fun configureStrictModePolicy() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .build()
        )
    }
}
