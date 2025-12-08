package com.machado001.lilol.common.model.data

data class ChampionDetails(
    val name: String,
    val id: String,
    val lore: String,
    val key: String,
    val title: String,
    val image: String,
    val tags: List<String>,
    val allytips: List<String>,
    val enemytips: List<String>,
    val passive: Passive,
    val spells: List<Spell>,
    val skins: List<Skin>
)
