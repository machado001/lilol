package com.machado001.lilol.rotation.model.repository

import com.machado001.lilol.rotation.model.network.DataDragonNetworkDataSource

class DataDragonRepositoryImpl(
    private val dataSource: DataDragonNetworkDataSource
) : DataDragonRepository {
    override suspend fun fetchDataDragon(version: String, region: String) =
        dataSource.fetchDataDragon(version, region)

    override suspend fun fetchAllGameVersions(): List<String> =
        dataSource.getAllGameVersion()

    override suspend fun fetchImage(version: String, imagePath: String) =
        dataSource.getImageByChampionImagePath(version, imagePath)
}