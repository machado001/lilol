package com.machado001.lilol.common.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.machado001.lilol.Rotation.LocalRotation
import com.machado001.lilol.common.Constants
import com.machado001.lilol.rotation.model.background.BackgroundTaskManager
import com.machado001.lilol.rotation.model.background.RotationBackgroundTaskManager
import com.machado001.lilol.rotation.model.background.RotationWorker
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
import java.util.concurrent.TimeUnit

class AppContainer(private val context: Context) : Container {

    private fun httpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(logging)
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
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
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


    private val workerRequest: PeriodicWorkRequest.Builder by lazy {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        PeriodicWorkRequestBuilder<RotationWorker>(
            WORK_REPEAT_INTERVAL, TimeUnit.DAYS,
            WORK_FLEX_TIME_INTERVAL_IN_MINUTES, TimeUnit.MINUTES,
        )
            .setConstraints(constraints)
            .addTag(TAG_WORK)
    }

    private val backgroundTaskManager: BackgroundTaskManager by lazy {
        val workManager = WorkManager.getInstance(context)
        RotationBackgroundTaskManager(workManager, workerRequest)
    }

    override val rotationRepository: RotationRepository by lazy {
        RotationRepositoryImpl(
            apiDataSource = rotationApi,
            localDataSource = rotationLocal,
            backgroundTaskManager = backgroundTaskManager
        )
    }

    override val dataDragonRepository: DataDragonRepository by lazy {
        DataDragonRepositoryImpl(dataSource = dataDragonApi)
    }

    override val championsManager: ChampionsManager by lazy {
        ChampionsManagerImpl(dataDragonRepository, rotationRepository)
    }

    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(SettingsLocalDataSourceImpl(context), dataDragonApi)
    }

    companion object {
        const val TAG_WORK = "Rotation"
        const val WORK_REPEAT_INTERVAL = 1L
        const val WORK_FLEX_TIME_INTERVAL_IN_MINUTES = 15L
        const val API_BASE_URL = "https://br1.api.riotgames.com/"
        const val DATASTORE_DESTINATION_FILE_NAME = "rotation.pb"
    }
}
