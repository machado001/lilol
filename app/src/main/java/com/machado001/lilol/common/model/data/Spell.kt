package com.machado001.lilol.common.model.data

data class Spell(
    val id: String,
    val name: String,
    val description: String,
    val iconUrl: String,
    val cooldownBurn: String,
    val costBurn: String,
    val rangeBurn: String?
)
