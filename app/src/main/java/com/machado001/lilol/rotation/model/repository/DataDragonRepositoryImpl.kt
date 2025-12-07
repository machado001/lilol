package com.machado001.lilol.rotation.model.repository

import androidx.annotation.GuardedBy
import com.machado001.lilol.rotation.model.dto.DataDragonDto
import com.machado001.lilol.rotation.model.dto.SpecificChampionDto
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
    private var cachedLanguage: String? = null

    @GuardedBy("mutex")
    private var allGamesVersion: List<String> = emptyList()

    @GuardedBy("mutex")
    private var allSupportedLanguages: List<String> = emptyList()

    @GuardedBy("mutex")
    private var cachedDetails: SpecificChampionDto? = null

    @GuardedBy("mutex")
    private var cachedDetailsLanguage: String? = null

    @GuardedBy("mutex")
    private var cachedImage: String? = null

    override suspend fun fetchDataDragon(version: String, region: String): DataDragonDto {
        val shouldFetch = mutex.withLock { 
            dataDragonDto == null || cachedLanguage != region 
        }

        if (shouldFetch) {
            val networkResult = dataSource.fetchDataDragon(version, region)
            mutex.withLock { 
                this@DataDragonRepositoryImpl.dataDragonDto = networkResult 
                this@DataDragonRepositoryImpl.cachedLanguage = region
            }
        }
        return mutex.withLock { dataDragonDto!! }
    }

    override suspend fun fetchAllGameVersions(): List<String> {
        val shouldFetch = mutex.withLock { allGamesVersion.isEmpty() }
        if (shouldFetch) {
            val networkResult = dataSource.getAllGameVersion()
            mutex.withLock {
                this@DataDragonRepositoryImpl.allGamesVersion = networkResult
            }

        }
        return mutex.withLock { allGamesVersion }
    }

    override suspend fun getAllSupportedLanguages(): List<String> {
        val shouldFetch = mutex.withLock { allSupportedLanguages.isEmpty() }
        if (shouldFetch) {
            val networkResult = dataSource.getSupportedLanguages()
            mutex.withLock {
                this@DataDragonRepositoryImpl.allSupportedLanguages = networkResult
            }
        }
        return mutex.withLock { allSupportedLanguages }
    }

    override suspend fun getSpecificChampion(
        version: String,
        lang: String,
        championName: String,
    ): SpecificChampionDto {
        val shouldFetch = mutex.withLock { 
            cachedDetails == null || 
            championName != cachedDetails?.data?.values?.firstOrNull()?.name ||
            cachedDetailsLanguage != lang
        }
        
        if (shouldFetch) {
            val networkResult =
                dataSource.getChampDetails(version, lang, championName)
            mutex.withLock { 
                cachedDetails = networkResult 
                cachedDetailsLanguage = lang
            }
        }
        return mutex.withLock { cachedDetails!! }
    }

    override suspend fun getSpecificChampionImage(
        championNameAsId: String,
    ): String {
        val shouldFetch = mutex.withLock { cachedImage == null }
        if (shouldFetch) {
            val networkResult =
                dataSource.getChampionSplashURL(championNameAsId)
            mutex.withLock { cachedImage = networkResult }
        }
        return mutex.withLock { cachedImage!! }
    }
}