package com.machado001.lilol.rotation.model.dto

data class ChampionDetailsDto(
    val id: String,
    val key: String,
    val name: String,
    val title: String,
    val image: Image,
    val skins: List<Skin> = emptyList(),
    val lore: String,
    val blurb: String,
    val allytips: List<String> = emptyList(),
    val enemytips: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val partype: String? = null,
    val info: Info? = null,
    val stats: Stats? = null,
    val passive: Passive? = null,
    val spells: List<Spell> = emptyList(),
    val recommended: List<Any?> = emptyList()
)
