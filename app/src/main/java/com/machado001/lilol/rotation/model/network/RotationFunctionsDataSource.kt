package com.machado001.lilol.rotation.model.network

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.machado001.lilol.rotation.model.dto.RotationsDto
import kotlinx.coroutines.tasks.await
import java.io.IOException

class RotationFunctionsDataSource(
    private val functions: FirebaseFunctions
) : RotationNetworkDataSource {

    override suspend fun fetchRotations(): RotationsDto {
        return try {
            val result = functions
                .getHttpsCallable("championRotation")
                .call()
                .await()

            val data = result.data as? Map<String, Any> ?: throw IOException("Invalid response format")
            
            val freeChampionIds = (data["freeChampionIds"] as? List<*>)?.filterIsInstance<Int>() ?: emptyList()
            val freeChampionIdsForNewPlayers = (data["freeChampionIdsForNewPlayers"] as? List<*>)?.filterIsInstance<Int>() ?: emptyList()
            val maxNewPlayerLevel = (data["maxNewPlayerLevel"] as? Number)?.toInt() ?: 0

            RotationsDto(
                freeChampionIds = freeChampionIds,
                freeChampionIdsForNewPlayers = freeChampionIdsForNewPlayers,
                maxNewPlayerLevel = maxNewPlayerLevel
            )
        } catch (e: FirebaseFunctionsException) {
            throw IOException("Firebase Functions error: ${e.code}", e)
        } catch (e: Exception) {
            throw IOException("Error fetching rotations", e)
        }
    }
}
