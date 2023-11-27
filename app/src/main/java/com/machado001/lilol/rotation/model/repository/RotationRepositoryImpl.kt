package com.machado001.lilol.rotation.model.repository

import androidx.work.WorkManager
import com.machado001.lilol.rotation.model.dto.RotationsDto
import com.machado001.lilol.rotation.model.network.RotationNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

private const val TAG_FETCH_LATEST_ROTATIONS = "FetchLatestRotationsTaskTag"
private const val FETCH_LATEST_ROTATIONS_TASK = "FetchLatestRotationsTask"


class RotationRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val apiDataSource: RotationNetworkDataSource,
) : RotationRepository {

    private val cacheMutex = Mutex()
    private var rotationsDto: RotationsDto? = null

    override suspend fun fetchRotations(refresh: Boolean): RotationsDto {
        if (rotationsDto == null || refresh) {
            withContext(ioDispatcher) {
                val networkResult = apiDataSource.fetchRotations()
                cacheMutex.withLock {
                    rotationsDto = networkResult
                }
            }
        }
        return rotationsDto!!
    }
}