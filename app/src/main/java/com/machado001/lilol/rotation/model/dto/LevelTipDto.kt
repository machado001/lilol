package com.machado001.lilol.rotation.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class LevelTipDto(
    val label: List<String>,
    val effect: List<String>
)
