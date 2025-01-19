package com.machado001.lilol.rotation.model.repository

import com.machado001.lilol.rotation.model.network.DataDragonNetworkDataSource

class SettingsRepositoryImpl(
    private val dataDragonDataSource: DataDragonNetworkDataSource
) : SettingsRepository {
    override suspend fun getApiLanguages() =
        dataDragonDataSource.getSupportedLanguages()
}