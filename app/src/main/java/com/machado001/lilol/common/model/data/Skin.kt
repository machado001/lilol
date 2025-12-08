package com.machado001.lilol.common.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Skin(
    val id: String,
    val num: Int,
    val name: String,
    val chromas: Boolean
) : Parcelable

