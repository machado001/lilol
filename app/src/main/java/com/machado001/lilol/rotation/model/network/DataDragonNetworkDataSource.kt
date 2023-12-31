package com.machado001.lilol.rotation.model.network

import com.machado001.lilol.rotation.model.dto.SpecificChampionDto
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

    @GET("cdn/languages.json")
    suspend fun getSupportedLanguages(): List<String>

    @GET("cdn/{version}/data/{lang}/champion/{champName}.json")
    suspend fun getChampDetails(
        @Path("version") version: String,
        @Path("lang") lang: String,
        @Path("champName") champName: String
    ): SpecificChampionDto

    @GET("cdn/img/champion/splash/{champIdAsName}_0.jpg")
    suspend fun getChampionSplashURL(
        @Path("champIdAsName") champIdAsName: String
    ): String

}
