package com.machado001.lilol.rotation.model.dto

import kotlinx.serialization.Serializable

/**
 * Represents the response data from the Riot Games API Data Dragon.
 *
 * @property version The current version of the Game.
 * @property format The format of the data.
 * @property type The type of data (e.g., "champion").
 * @property data A map of champion, key-value is <ChampionName,ChampionInfo's>
 */
@Serializable
data class DataDragonDto(
    val version: String,
    val format: String,
    val type: String,
    val data: Map<String, ChampionDto>
)



