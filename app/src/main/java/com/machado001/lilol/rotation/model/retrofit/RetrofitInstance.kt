package com.machado001.lilol.rotation.model.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitInstance {

    const val API_KEY = "RGAPI-63dcff38-573f-41a0-b085-2ecdea47a375"

    val api: RotationRemoteDataSource by lazy {
        Retrofit.Builder()
            .baseUrl("https://br1.api.riotgames.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }

}