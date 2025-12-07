package com.machado001.lilol.rotation.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChampionDto(
    val blurb: String,
    val id: String,
    val image: ImageDto,
    val info: InfoDto,
    val key: String,
    val name: String,
    val partype: String,
    val stats: StatsDto,
    val tags: List<String>,
    val title: String,
    val version: String
)







