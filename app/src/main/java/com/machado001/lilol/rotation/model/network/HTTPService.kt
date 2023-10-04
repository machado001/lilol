package com.machado001.lilol.rotation.model.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object HTTPService {

    private fun httpClient(): OkHttpClient {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    val rotationApi: RotationNetworkDataSource by lazy {
        Retrofit.Builder()
            .baseUrl("https://br1.api.riotgames.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient())
            .build()
            .create()
    }

    val championApi: DataDragonNetworkDataSource by lazy {
        Retrofit.Builder()
            .baseUrl("https://ddragon.leagueoflegends.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient())
            .build()
            .create()
    }


}


