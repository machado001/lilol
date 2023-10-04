package com.machado001.lilol.common.extensions

import com.machado001.lilol.common.model.data.Champion
import com.machado001.lilol.common.model.data.DataDragon
import com.machado001.lilol.rotation.model.dto.ChampionDto
import com.machado001.lilol.rotation.model.dto.DataDragonDto
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}

fun ChampionDto.toChampion() : Champion = Champion(
    id = key,
    name = name,
    image = image.full
)

fun DataDragonDto.toDataDragon(): DataDragon = DataDragon(
    data = data.mapValues { it.value.toChampion() },
    version = version
)