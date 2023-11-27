package com.machado001.lilol.rotation.model.repository

import com.machado001.lilol.rotation.model.dto.DataDragonDto
import com.machado001.lilol.rotation.model.dto.RotationsDto

class ChampionsManagerImpl(
    private val dataDragonRepository: DataDragonRepository,
    private val rotationRepository: RotationRepository,
) : ChampionsManager {

    override suspend fun getSupportedLanguages() = dataDragonRepository.getAllSupportedLanguages()
    override suspend fun getCurrentVersion() = dataDragonRepository.fetchAllGameVersions().first()
    override suspend fun getDataDragon(language: String): DataDragonDto =
        dataDragonRepository.fetchDataDragon(getCurrentVersion(), language)

    override suspend fun getRotations(): RotationsDto {
        return rotationRepository.run {
            fetchRotations()
        }
    }
}