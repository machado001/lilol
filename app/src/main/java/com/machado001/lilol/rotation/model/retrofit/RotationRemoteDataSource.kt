package com.machado001.lilol.rotation.model.retrofit

import com.machado001.lilol.BuildConfig
import com.machado001.lilol.rotation.model.Rotations
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RotationRemoteDataSource {
    @GET("/lol/platform/v3/champion-rotations")
    suspend fun fetchRotations(
        @Query("api_key") apiKey: String = BuildConfig.API_ROTATIONS_KEY
    ): Response<Rotations>
}