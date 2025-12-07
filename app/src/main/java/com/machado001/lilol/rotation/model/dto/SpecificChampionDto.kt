package com.machado001.lilol.rotation.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class SpecificChampionDto(
    val type: String,
    val format: String,
    val version: String,
    val data: Map<String, ChampionDetailsDto>
)
