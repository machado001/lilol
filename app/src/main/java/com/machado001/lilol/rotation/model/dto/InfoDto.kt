package com.machado001.lilol.rotation.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class InfoDto(
    val attack: Int,
    val defense: Int,
    val difficulty: Int,
    val magic: Int
)