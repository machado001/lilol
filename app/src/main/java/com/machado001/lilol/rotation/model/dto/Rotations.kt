package com.machado001.lilol.rotation.model.dto

data class Rotations(
    val freeChampionIds: List<Int>,
    val freeChampionIdsForNewPlayers: List<Int>,
    val maxNewPlayerLevel: Int
)
