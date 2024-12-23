package com.machado001.lilol.rotation.model.repository

import com.machado001.lilol.rotation.model.dto.RotationsDto
import kotlinx.coroutines.flow.Flow

interface RotationRepository {
    suspend fun fetchRemoteRotations(refresh: Boolean = false): RotationsDto
    suspend fun fetchLocalRotations(): Flow<String>
}