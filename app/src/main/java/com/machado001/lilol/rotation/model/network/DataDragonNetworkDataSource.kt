package com.machado001.lilol.rotation.model.network

import com.machado001.lilol.rotation.model.dto.DataDragonDto
import retrofit2.http.GET
import retrofit2.http.Path

interface DataDragonNetworkDataSource {

    @GET("cdn/{version}/data/{region}/champion.json")
    suspend fun fetchDataDragon(
        @Path("version") version: String,
        @Path("region") region: String
    ): DataDragonDto

    @GET("api/versions.json")
    suspend fun getAllGameVersion(): List<String>

    @GET("cdn/{version}/img/champion/{imagePath}")
    suspend fun getImageByChampionImagePath(
        @Path("version") version: String,
        @Path("imagePath") imagePath: String,

    ): String

}
