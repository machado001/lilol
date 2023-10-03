package com.machado001.lilol.rotation.model

import com.machado001.lilol.common.base.RequestCallback

class RotationRepository(
    private val dataSource: RotationDataSource
) {

    suspend fun fetchRotations(callback: RequestCallback<Rotations>) {
        dataSource.fetchRotations(callback)
    }
}