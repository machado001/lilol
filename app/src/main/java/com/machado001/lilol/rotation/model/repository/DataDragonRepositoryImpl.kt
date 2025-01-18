package com.machado001.lilol.rotation.model.repository

import androidx.annotation.GuardedBy
import com.machado001.lilol.rotation.model.dto.DataDragonDto
import com.machado001.lilol.rotation.model.dto.SpecificChampionDto
import com.machado001.lilol.rotation.model.network.DataDragonNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class DataDragonRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val dataSource: DataDragonNetworkDataSource,
) : DataDragonRepository {

    private var mutex = Mutex()

    @GuardedBy("mutex")
    private var dataDragonDto: DataDragonDto? = null

    @GuardedBy("mutex")
    private var allGamesVersion: List<String> = emptyList()

    @GuardedBy("mutex")
    private var allSupportedLanguages: List<String> = emptyList()

    @GuardedBy("mutex")
    private var cachedDetails: SpecificChampionDto? = null

    @GuardedBy("mutex")
    private var cachedImage: String? = null

    override suspend fun fetchDataDragon(version: String, region: String): DataDragonDto {

        if (dataDragonDto == null) {
            val networkResult = dataSource.fetchDataDragon(version, region)
            mutex.withLock { this@DataDragonRepositoryImpl.dataDragonDto = networkResult }
        }
        return mutex.withLock { dataDragonDto!! }
    }

    override suspend fun fetchAllGameVersions(): List<String> {
        if (allGamesVersion.isEmpty()) {
            val networkResult = dataSource.getAllGameVersion()
            mutex.withLock {
                this@DataDragonRepositoryImpl.allGamesVersion = networkResult
            }

        }
        return mutex.withLock { allGamesVersion }
    }

    override suspend fun getAllSupportedLanguages(): List<String> {
        if (allSupportedLanguages.isEmpty()) {

            withContext(ioDispatcher) {
                val networkResult = dataSource.getSupportedLanguages()
                mutex.withLock {
                    this@DataDragonRepositoryImpl.allSupportedLanguages = networkResult
                }
            }
        }
        return mutex.withLock { allSupportedLanguages }
    }

    override suspend fun getSpecificChampion(
        version: String,
        lang: String,
        championName: String,
    ): SpecificChampionDto {
        if (cachedDetails == null || championName != cachedDetails?.data?.values?.first()?.name) {
            withContext(ioDispatcher) {
                val networkResult =
                    dataSource.getChampDetails(version, lang, championName)
                mutex.withLock { cachedDetails = networkResult }
            }
        }
        return mutex.withLock { cachedDetails!! }
    }

    override suspend fun getSpecificChampionImage(
        championNameAsId: String,
    ): String {
        if (cachedImage == null) {
            withContext(ioDispatcher) {
                val networkResult =
                    dataSource.getChampionSplashURL(championNameAsId)
                mutex.withLock { cachedImage = networkResult }
            }
        }
        return mutex.withLock { cachedImage!! }
    }
}