package com.machado001.lilol.rotation.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class SkinDto(
    val id: String,
    val num: Int,
    val name: String,
    val chromas: Boolean
)
