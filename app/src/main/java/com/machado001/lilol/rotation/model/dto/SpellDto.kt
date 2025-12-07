package com.machado001.lilol.rotation.model.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class SpellDto(
    val id: String,
    val name: String,
    val description: String,
    val tooltip: String,
    val leveltip: LevelTipDto? = null,
    val maxrank: Int,
    val cooldown: List<Float>,
    val cooldownBurn: String,
    val cost: List<Int>,
    val costBurn: String,
    val datavalues: Map<String, JsonElement>,
    val effect: List<List<Float>?>,
    val effectBurn: List<String?>,
    val vars: List<JsonElement>,
    val costType: String,
    val maxammo: String,
    val range: List<Long>,
    val rangeBurn: String,
    val image: ImageDto,
    val resource: String? = null
)
