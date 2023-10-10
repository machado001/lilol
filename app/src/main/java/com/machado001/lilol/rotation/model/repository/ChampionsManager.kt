package com.machado001.lilol.rotation.model.repository

import com.machado001.lilol.rotation.model.dto.DataDragonDto
import com.machado001.lilol.rotation.model.dto.RotationsDto

/**
 * Manages RotationRepository and DataDragonRepository in one source
 */
interface ChampionsManager {
    suspend fun getSupportedLanguages(): List<String>
    suspend fun getCurrentVersion(): String
    suspend fun getDataDragon(language: String): DataDragonDto
    suspend fun getRotations(): RotationsDto
}

