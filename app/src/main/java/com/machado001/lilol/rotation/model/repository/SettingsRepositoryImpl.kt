package com.machado001.lilol.rotation.model.repository

import android.util.Log
import com.machado001.lilol.rotation.model.local.SettingsLocalDataSource
import com.machado001.lilol.rotation.model.network.DataDragonNetworkDataSource
import java.util.Locale

class SettingsRepositoryImpl
    (
    private val slds: SettingsLocalDataSource,
    private val dataDragonDataSource: DataDragonNetworkDataSource
) : SettingsRepository {
    override fun getAppLanguage(): String {
        return try {
            slds.getAppLanguage()
        } catch (e: ClassCastException) {
            e.message?.let { Log.e("TAG", it) }
            Locale.getDefault().toString()
        }
    }

    override suspend fun getApiLanguages() = dataDragonDataSource.getSupportedLanguages()
}