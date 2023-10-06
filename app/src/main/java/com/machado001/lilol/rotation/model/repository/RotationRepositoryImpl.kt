package com.machado001.lilol.rotation.model.repository

import com.machado001.lilol.rotation.model.dto.RotationsDto
import com.machado001.lilol.rotation.model.network.RotationNetworkDataSource
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class RotationRepositoryImpl(
    private val apiDataSource: RotationNetworkDataSource
) : RotationRepository {

    private val cacheMutex = Mutex()
    private var rotationsDto: RotationsDto? = null
    override suspend fun fetchRotations(): RotationsDto {
        if (rotationsDto == null) {
            val networkResult = apiDataSource.fetchRotations()
            cacheMutex.withLock {
                rotationsDto = networkResult
            }
        }
        return rotationsDto!!
    }
}