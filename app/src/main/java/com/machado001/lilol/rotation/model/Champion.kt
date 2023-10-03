package com.machado001.lilol.rotation.model

import androidx.annotation.DrawableRes

data class Champion(
    val id: String,
    val name: String,
    @DrawableRes val image: Int
)
