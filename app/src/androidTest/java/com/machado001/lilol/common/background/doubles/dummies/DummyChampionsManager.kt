package com.machado001.lilol.common.background.doubles.dummies

import com.machado001.lilol.rotation.model.dto.DataDragonDto
import com.machado001.lilol.rotation.model.dto.RotationsDto
import com.machado001.lilol.rotation.model.repository.ChampionsManager

class DummyChampionsManager : ChampionsManager {
    override suspend fun getSupportedLanguages(): List<String> {
        return emptyList()
    }

    override suspend fun getCurrentVersion(): String {
        return ""
    }

    override suspend fun getDataDragon(language: String): DataDragonDto {
        return DataDragonDto("", "", "", HashMap())
    }

    override suspend fun getRotations(): RotationsDto {
        return RotationsDto(emptyList(), emptyList(),20)
    }
}