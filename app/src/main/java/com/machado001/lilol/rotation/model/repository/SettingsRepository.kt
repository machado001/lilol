package com.machado001.lilol.rotation.model.repository

interface SettingsRepository {
    suspend fun getApiLanguages(): List<String>
}