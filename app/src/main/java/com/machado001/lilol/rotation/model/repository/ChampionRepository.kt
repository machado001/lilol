package com.machado001.lilol.rotation.model.repository

import com.machado001.lilol.rotation.model.dto.DataDragonDto
import com.machado001.lilol.rotation.model.dto.RotationsDto

class ChampionRepository
    (
    private val dataDragonRepository: DataDragonRepository,
    private val rotationRepository: RotationRepository
) { suspend fun getCurrentVersion() = dataDragonRepository.fetchAllGameVersions().first()
    suspend fun getDataDragon(region: String): DataDragonDto =
        dataDragonRepository.fetchDataDragon(getCurrentVersion(), region)

    suspend fun getRotations(): RotationsDto = rotationRepository.fetchRotations()

}