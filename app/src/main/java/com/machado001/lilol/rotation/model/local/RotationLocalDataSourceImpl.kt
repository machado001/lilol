package com.machado001.lilol.rotation.model.local

import androidx.datastore.core.DataStore
import com.machado001.lilol.Rotation
import com.machado001.lilol.rotation.model.dto.Rotations
import com.machado001.lilol.rotation.model.dto.RotationsDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RotationLocalDataSourceImpl(private val dataStore: DataStore<Rotation.LocalRotation>) :
    RotationLocalDataSource {

    private val json = Json { ignoreUnknownKeys = true }

    override val rotation: Flow<String> = dataStore.data.map { rotation ->
        rotation.rotationCurrent
    }

    override suspend fun setRotation(remoteRotation: Rotations) {
        dataStore.updateData { currentRotation ->
            val storedString = json.encodeToString(
                RotationsDto(
                    freeChampionIds = remoteRotation.freeChampionIds,
                    freeChampionIdsForNewPlayers = remoteRotation.freeChampionIdsForNewPlayers,
                    maxNewPlayerLevel = remoteRotation.maxNewPlayerLevel
                )
            )
            currentRotation.toBuilder()
                .setRotationCurrent(storedString)
                .build()
        }
    }

    override suspend fun getLocalRotation(): RotationsDto? {
        val raw = dataStore.data.firstOrNull()?.rotationCurrent ?: return null
        return decodeRotations(raw)
    }

    private fun decodeRotations(raw: String): RotationsDto? {
        return try {
            json.decodeFromString(RotationsDto.serializer(), raw)
        } catch (_: Exception) {
            parseLegacy(raw)
        }
    }

    private fun parseLegacy(raw: String): RotationsDto? {
        val freeRegex = Regex("freeChampionIds=\\[(.*?)]")
        val newPlayersRegex = Regex("freeChampionIdsForNewPlayers=\\[(.*?)]")
        val levelRegex = Regex("maxNewPlayerLevel=([0-9]+)")

        val freeIds = freeRegex.find(raw)?.groupValues?.getOrNull(1)
            ?.split(",")
            ?.mapNotNull { it.trim().toIntOrNull() }
            ?: emptyList()

        val newPlayerIds = newPlayersRegex.find(raw)?.groupValues?.getOrNull(1)
            ?.split(",")
            ?.mapNotNull { it.trim().toIntOrNull() }
            ?: emptyList()

        val level = levelRegex.find(raw)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0

        if (freeIds.isEmpty()) return null

        return RotationsDto(
            freeChampionIds = freeIds,
            freeChampionIdsForNewPlayers = newPlayerIds,
            maxNewPlayerLevel = level
        )
    }
}
