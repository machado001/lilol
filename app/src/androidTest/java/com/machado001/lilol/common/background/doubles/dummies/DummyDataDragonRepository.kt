package com.machado001.lilol.common.background.doubles.dummies

import com.machado001.lilol.rotation.model.dto.DataDragonDto
import com.machado001.lilol.rotation.model.dto.SpecificChampionDto
import com.machado001.lilol.rotation.model.repository.DataDragonRepository

class DummyDataDragonRepository : DataDragonRepository {
    override suspend fun fetchDataDragon(version: String, region: String): DataDragonDto {
        return DataDragonDto("", "", "", HashMap())
    }

    override suspend fun fetchAllGameVersions(): List<String> {
        return emptyList()
    }

    override suspend fun getAllSupportedLanguages(): List<String> {
        return emptyList()
    }

    override suspend fun getSpecificChampion(
        version: String,
        lang: String,
        championName: String
    ): SpecificChampionDto {
        return SpecificChampionDto("", "", "", HashMap())
    }

    override suspend fun getSpecificChampionImage(championNameAsId: String): String {
        return ""
    }

}
