package com.machado001.lilol.rotation.model.repository

import com.machado001.lilol.rotation.model.dto.RotationsDto

interface RotationRepository {
    suspend fun fetchRotations() : RotationsDto
}