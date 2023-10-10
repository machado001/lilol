package com.machado001.lilol.rotation.model.network

import com.machado001.lilol.BuildConfig
import com.machado001.lilol.rotation.model.dto.RotationsDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RotationNetworkDataSource {
    @GET("lol/platform/v3/champion-rotations")
    suspend fun fetchRotations(
        @Query("api_key") apiKey: String = BuildConfig.API_ROTATIONS_KEY
    ): RotationsDto
}