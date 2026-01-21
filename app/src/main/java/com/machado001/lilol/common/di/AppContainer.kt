package com.machado001.lilol.common.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.Firebase
import com.google.firebase.functions.functions
import com.machado001.google.inappreview.GooglePlayReviewManager
import com.machado001.lilol.BuildConfig
import com.machado001.lilol.Rotation.LocalRotation
import com.machado001.lilol.common.Constants
import com.machado001.lilol.rotation.model.local.RotationLocalDataSource
import com.machado001.lilol.rotation.model.local.RotationLocalDataSourceImpl
import com.machado001.lilol.rotation.model.local.RotationSerializer
import com.machado001.lilol.rotation.model.network.DataDragonNetworkDataSource
import com.machado001.lilol.rotation.model.network.RotationFunctionsDataSource
import com.machado001.lilol.rotation.model.network.RotationNetworkDataSource
import com.machado001.lilol.rotation.model.repository.ChampionsManager
import com.machado001.lilol.rotation.model.repository.ChampionsManagerImpl
import com.machado001.lilol.rotation.model.repository.DataDragonRepository
import com.machado001.lilol.rotation.model.repository.DataDragonRepositoryImpl
import com.machado001.lilol.rotation.model.repository.RotationRepository
import com.machado001.lilol.rotation.model.repository.RotationRepositoryImpl
import com.machado001.lilol.rotation.model.repository.SettingsRepository
import com.machado001.lilol.rotation.model.repository.SettingsRepositoryImpl
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

class AppContainer(private val context: Context) : Container {

    private val httpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }

        OkHttpClient.Builder()
            .apply { if (BuildConfig.DEBUG) addInterceptor(logging) }
            .build()
    }

    private val Context.rotationDataStore: DataStore<LocalRotation> by dataStore(
        fileName = DATASTORE_DESTINATION_FILE_NAME,
        serializer = RotationSerializer
    )

    private val rotationLocal: RotationLocalDataSource by lazy {
        RotationLocalDataSourceImpl(context.rotationDataStore)
    }

    private val rotationApi: RotationNetworkDataSource by lazy {
        RotationFunctionsDataSource(Firebase.functions)
    }

    private val networkJson = Json { ignoreUnknownKeys = true }

    private val dataDragonApi: DataDragonNetworkDataSource by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.DATA_DRAGON_BASE_URL)
            .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
            .client(httpClient)
            .build()
            .create()
    }

    override val rotationRepository: RotationRepository by lazy {
        RotationRepositoryImpl(
            apiDataSource = rotationApi,
            localDataSource = rotationLocal
        )
    }

    override val dataDragonRepository: DataDragonRepository by lazy {
        DataDragonRepositoryImpl(dataSource = dataDragonApi)
    }

    override val championsManager: ChampionsManager by lazy {
        ChampionsManagerImpl(dataDragonRepository, rotationRepository)
    }

    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(dataDragonApi)
    }

    override val reviewManager: GooglePlayReviewManager by lazy {
        GooglePlayReviewManager(ReviewManagerFactory.create(context))
    }

    companion object {
        const val DATASTORE_DESTINATION_FILE_NAME = "rotation.pb"
    }
}
