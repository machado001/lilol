package com.machado001.lilol.rotation.model.network

import com.machado001.lilol.rotation.model.dto.DataDragonDto
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface DataDragonNetworkDataSource {

    @Headers("Cache-Control: max-age=3600")
    @GET("cdn/{version}/data/{region}/champion.json")
    suspend fun fetchDataDragon(
        @Path("version") version: String,
        @Path("region") region: String
    ): DataDragonDto

    @Headers("Cache-Control: max-age=3600")
    @GET("api/versions.json")
    suspend fun getAllGameVersion(): List<String>

    @Headers("Cache-Control: max-age=3600")
    @GET("cdn/languages.json")
    suspend fun getSupportedLanguages(): List<String>

}
