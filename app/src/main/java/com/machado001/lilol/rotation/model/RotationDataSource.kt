package com.machado001.lilol.rotation.model

import com.machado001.lilol.common.base.RequestCallback

interface RotationDataSource {
    suspend fun fetchRotations(callback: RequestCallback<Rotations>)
}