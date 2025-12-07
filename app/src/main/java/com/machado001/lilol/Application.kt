package com.machado001.lilol

import android.app.Application
import android.os.StrictMode
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.machado001.lilol.common.di.AppContainer
import com.machado001.lilol.common.di.Container

/**
 * Application to apply Manual DI.
 */
class Application : Application() {

    lateinit var container: Container

    override fun onCreate() {
        super.onCreate()
        configureStrictModePolicy()
        initAppCheck()
        val appContainer = AppContainer(this)
        MyNotification(this).createNotificationChannel()
        container = appContainer
    }

    private fun initAppCheck() {
        Firebase.appCheck.installAppCheckProviderFactory(
            if (BuildConfig.DEBUG) {
                DebugAppCheckProviderFactory.getInstance()
            } else {
                PlayIntegrityAppCheckProviderFactory.getInstance()
            }
        )
    }

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
