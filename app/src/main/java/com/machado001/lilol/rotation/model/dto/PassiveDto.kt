package com.machado001.lilol.rotation.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class PassiveDto(
    val name: String,
    val description: String,
    val image: ImageDto,
)
