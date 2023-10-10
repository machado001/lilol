package com.machado001.lilol.rotation.model.repository

import com.machado001.lilol.rotation.model.dto.SpecificChampionDto
import com.machado001.lilol.rotation.model.dto.DataDragonDto

interface DataDragonRepository {
    suspend fun fetchDataDragon(version: String, region: String): DataDragonDto
    suspend fun fetchAllGameVersions(): List<String>
    suspend fun getAllSupportedLanguages(): List<String>

    suspend fun getSpecificChampion(
        version: String,
        lang: String,
        championName: String,
    ): SpecificChampionDto

    suspend fun getSpecificChampionImage(
        championNameAsId: String
    ): String
}