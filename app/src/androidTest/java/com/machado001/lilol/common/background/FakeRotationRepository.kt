package com.machado001.lilol.common.background

import com.machado001.lilol.rotation.model.dto.RotationsDto
import com.machado001.lilol.rotation.model.repository.RotationRepository

class FakeRotationRepository : RotationRepository {
    override suspend fun fetchRotations(refresh: Boolean): RotationsDto {
        return RotationsDto(
            listOf(1, 2, 3, 4, 5, 6, 7, 8),
            listOf(9, 10, 11, 12, 13, 14, 15, 16),
            30
        )
    }

}
