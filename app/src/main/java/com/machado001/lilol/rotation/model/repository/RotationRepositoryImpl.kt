package com.machado001.lilol.rotation.model.repository

import com.machado001.lilol.rotation.model.network.RotationNetworkDataSource
class RotationRepositoryImpl(
    private val apiDataSource: RotationNetworkDataSource
) : RotationRepository {
    override suspend fun fetchRotations()=  apiDataSource.fetchRotations()
}