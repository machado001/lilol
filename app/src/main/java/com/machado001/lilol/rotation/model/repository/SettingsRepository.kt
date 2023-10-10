package com.machado001.lilol.rotation.model.repository

/**
 * @property getAppLanguage - Get the app language registered in the local data source.
 *
 */
interface SettingsRepository {

    fun getAppLanguage(): String

    suspend fun getApiLanguages(): List<String>
}