package com.machado001.lilol.common.model.data

import com.machado001.lilol.rotation.model.dto.ImageDto

data class Spell(
    val id: String,
    val name: String,
    val description: String,
    val image: ImageDto,
)
