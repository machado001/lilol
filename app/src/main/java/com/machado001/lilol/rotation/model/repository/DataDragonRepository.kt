package com.machado001.lilol.rotation.model.repository

import com.machado001.lilol.rotation.model.dto.DataDragonDto

interface DataDragonRepository {
    suspend fun fetchDataDragon(version: String, region: String): DataDragonDto

    suspend fun fetchAllGameVersions(): List<String>

    suspend fun fetchImage(version: String, imagePath: String): String
}