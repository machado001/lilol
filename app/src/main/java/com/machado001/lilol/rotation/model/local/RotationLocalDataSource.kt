package com.machado001.lilol.rotation.model.local

import com.machado001.lilol.rotation.model.dto.Rotations
import kotlinx.coroutines.flow.Flow

interface RotationLocalDataSource {
    val rotation: Flow<String>
    suspend fun setRotation(remoteRotation: Rotations)
    suspend fun getLocalRotation(): com.machado001.lilol.rotation.model.dto.RotationsDto?
}
