package com.machado001.lilol.rotation.model.dto

data class ChampionDetailsDto(
    val id: String,
    val key: String,
    val name: String,
    val title: String,
    val image: ImageDto,
    val skins: List<SkinDto> = emptyList(),
    val lore: String,
    val blurb: String,
    val allytips: List<String> = emptyList(),
    val enemytips: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val partype: String? = null,
    val info: InfoDto? = null,
    val stats: StatsDto? = null,
    val passive: PassiveDto? = null,
    val spells: List<SpellDto> = emptyList(),
    val recommended: List<Any?> = emptyList()
)
