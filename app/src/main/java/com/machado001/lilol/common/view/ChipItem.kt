package com.machado001.lilol.common.view

import android.content.Context
import android.view.ContextThemeWrapper
import com.google.android.material.R
import com.google.android.material.chip.Chip

data class FilterItem(
    val text: String,
)

fun FilterItem.toChip(context: Context): Chip {
    val chip = Chip(ContextThemeWrapper(context, R.style.Widget_Material3_Chip_Filter))
    chip.text = text

    return chip
}


