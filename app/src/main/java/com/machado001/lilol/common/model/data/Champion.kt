package com.machado001.lilol.common.model.data

data class Champion(
    val key: String,
    val id: String,
    val name: String,
    val image: String,
    val tags: List<String>,
    val version: String
)
