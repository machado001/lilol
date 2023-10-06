package com.machado001.lilol.rotation.model.repository

import androidx.annotation.GuardedBy
import com.machado001.lilol.rotation.model.dto.DataDragonDto
import com.machado001.lilol.rotation.model.network.DataDragonNetworkDataSource
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DataDragonRepositoryImpl(
    private val dataSource: DataDragonNetworkDataSource,
) : DataDragonRepository {

    private var mutex = Mutex()

    @GuardedBy("mutex")
    private var dataDragonDto: DataDragonDto? = null

    @GuardedBy("mutex")
    private var allGamesVersion: List<String> = emptyList()

    @GuardedBy("mutex")
    private var allSupportedLanguages: List<String> = emptyList()

    override suspend fun fetchDataDragon(version: String, region: String): DataDragonDto {
        if (dataDragonDto == null) {
            val networkResult = dataSource.fetchDataDragon(version, region)
            mutex.withLock { this.dataDragonDto = networkResult }
        }

        return dataDragonDto!!
    }

    override suspend fun fetchAllGameVersions(): List<String> {

        if (allGamesVersion.isEmpty()) {
            val networkResult = dataSource.getAllGameVersion()
            mutex.withLock {
                this.allGamesVersion = networkResult
            }
        }

        return mutex.withLock { allGamesVersion }
    }

    override suspend fun getAllSupportedLanguages(): List<String> {
        if (allSupportedLanguages.isEmpty()) {
            val networkResult = dataSource.getSupportedLanguages()
            mutex.withLock {
                this.allSupportedLanguages = networkResult
            }
        }
        return mutex.withLock { allSupportedLanguages }
    }
}