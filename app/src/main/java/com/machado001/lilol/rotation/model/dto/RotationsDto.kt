package com.machado001.lilol.rotation.model.dto

data class RotationsDto(
    val freeChampionIds: List<Int>,
    val freeChampionIdsForNewPlayers: List<Int>,
    val maxNewPlayerLevel: Int
)

//just to maintain a pattern. it transformation wouldn't be necessary.
