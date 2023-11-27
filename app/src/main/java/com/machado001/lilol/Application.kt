package com.machado001.lilol

import android.app.Application
import com.google.firebase.FirebaseApp
import com.machado001.lilol.common.di.AppContainer

/**
 * Application to apply Manual DI.
 */
class Application : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        val appContainer = AppContainer(this)
        super.onCreate()
        container = appContainer
    }


}
