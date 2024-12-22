package com.machado001.lilol.rotation.model.local

import androidx.datastore.core.DataStore
import com.machado001.lilol.Rotation
import com.machado001.lilol.rotation.model.dto.Rotations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RotationLocalDataSourceImpl(private val dataStore: DataStore<Rotation.LocalRotation>) :
    RotationLocalDataSource {

    override val rotation: Flow<String> = dataStore.data.map { rotation ->
        rotation.rotationCurrent
    }

    override suspend fun setRotation(remoteRotation: Rotations) {
        dataStore.updateData { currentRotation ->
            currentRotation.toBuilder()
                .setRotationCurrent(remoteRotation.toString())
                .build()
        }
    }
}