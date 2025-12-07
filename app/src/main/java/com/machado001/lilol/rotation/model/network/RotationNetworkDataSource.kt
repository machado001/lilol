package com.machado001.lilol.rotation.model.network

import com.machado001.lilol.rotation.model.dto.RotationsDto

interface RotationNetworkDataSource {
    suspend fun fetchRotations(): RotationsDto
}
