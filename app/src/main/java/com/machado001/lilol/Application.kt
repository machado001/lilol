package com.machado001.lilol

import android.app.Application
import com.machado001.lilol.common.di.AppContainer

class Application : Application() {

    lateinit var container: AppContainer
    override fun onCreate() {
        val appContainer = AppContainer(this)
        super.onCreate()
        container = appContainer
    }
}
