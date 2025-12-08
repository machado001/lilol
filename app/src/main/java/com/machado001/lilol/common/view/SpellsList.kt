package com.machado001.lilol.common.view

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SpellListItem(
    val id: String,
    val keyboardKey: Char,
    val iconUrl: String,
    val name: String,
    val description: String,
    val cooldownBurn: String,
    val costBurn: String,
    val rangeBurn: String?
) : Parcelable

// [P] [Q] [W] [E] [R]
