package com.machado001.lilol.common.background.doubles.fakes

import com.machado001.lilol.rotation.model.dto.RotationsDto
import com.machado001.lilol.rotation.model.repository.RotationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class FakeRotationRepository : RotationRepository {
    override suspend fun fetchRemoteRotations(refresh: Boolean): RotationsDto {
        return RotationsDto(
            listOf(1, 2, 3, 4, 5, 6, 7, 8),
            listOf(9, 10, 11, 12, 13, 14, 15, 16),
            30
        )
    }

    override val localRotations: Flow<String> =
        listOf(
            """
             Rotations(
             freeChampionIds=[10, 20, 30, 40, 50, 6, 7, 8],
             freeChampionIdsForNewPlayers=[9, 10, 11, 12, 13, 14, 15, 16], 
             maxNewPlayerLevel=30
              )
         """.trimIndent()
        ).asFlow()


}
