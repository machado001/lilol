package com.machado001.lilol.common.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.machado001.lilol.Rotation.LocalRotation
import com.machado001.lilol.common.Constants
import com.machado001.lilol.rotation.model.local.RotationLocalDataSource
import com.machado001.lilol.rotation.model.local.RotationLocalDataSourceImpl
import com.machado001.lilol.rotation.model.local.RotationSerializer
import com.machado001.lilol.rotation.model.local.SettingsLocalDataSourceImpl
import com.machado001.lilol.rotation.model.network.DataDragonNetworkDataSource
import com.machado001.lilol.rotation.model.network.RotationNetworkDataSource
import com.machado001.lilol.rotation.model.repository.ChampionsManager
import com.machado001.lilol.rotation.model.repository.ChampionsManagerImpl
import com.machado001.lilol.rotation.model.repository.DataDragonRepository
import com.machado001.lilol.rotation.model.repository.DataDragonRepositoryImpl
import com.machado001.lilol.rotation.model.repository.RotationRepository
import com.machado001.lilol.rotation.model.repository.RotationRepositoryImpl
import com.machado001.lilol.rotation.model.repository.SettingsRepository
import com.machado001.lilol.rotation.model.repository.SettingsRepositoryImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class AppContainer(private val context: Context) {

    private fun httpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    private val Context.rotationDataStore: DataStore<LocalRotation> by dataStore(
        fileName = "rotation.pb",
        serializer = RotationSerializer
    )

    private val rotationLocal: RotationLocalDataSource by lazy {
        RotationLocalDataSourceImpl(context.rotationDataStore)
    }

    private val rotationApi: RotationNetworkDataSource by lazy {
        Retrofit.Builder()
            .baseUrl("https://br1.api.riotgames.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient())
            .build()
            .create()
    }

    private val dataDragonApi: DataDragonNetworkDataSource by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.DATA_DRAGON_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient())
            .build()
            .create()
    }

    private val rotationRepository: RotationRepository by lazy {
        RotationRepositoryImpl(
            apiDataSource = rotationApi,
            localDataSource = rotationLocal,
            context = context
        )
    }

    val dataDragonRepository: DataDragonRepository by lazy {
        DataDragonRepositoryImpl(dataSource = dataDragonApi)
    }

    val championsManager: ChampionsManager by lazy {
        ChampionsManagerImpl(dataDragonRepository, rotationRepository)
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(SettingsLocalDataSourceImpl(context), dataDragonApi)
    }
}
