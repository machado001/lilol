package com.machado001.lilol.rotation.model.dto

data class SpellDto(
    val id: String,
    val name: String,
    val description: String,
    val tooltip: String,
    val leveltip: LevelTipDto,
    val maxrank: Int,
    val cooldown: List<Float>,
    val cooldownBurn: String,
    val cost: List<Int>,
    val costBurn: String,
    val datavalues: Map<String, Any>,
    val effect: List<List<Float>?>,
    val effectBurn: List<String?>,
    val vars: List<Any>,
    val costType: String,
    val maxammo: String,
    val range: List<Int>,
    val rangeBurn: String,
    val image: ImageDto,
    val resource: String
)
