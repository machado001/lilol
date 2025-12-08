package com.machado001.lilol.common.extensions

import com.machado001.lilol.common.model.data.Champion
import com.machado001.lilol.common.model.data.ChampionDetails
import com.machado001.lilol.common.model.data.DataDragon
import com.machado001.lilol.common.model.data.Spell
import com.machado001.lilol.rotation.model.dto.ChampionDto
import com.machado001.lilol.rotation.model.dto.DataDragonDto
import com.machado001.lilol.rotation.model.dto.Rotations
import com.machado001.lilol.rotation.model.dto.RotationsDto
import com.machado001.lilol.rotation.model.dto.SpecificChampionDto
import com.machado001.lilol.rotation.model.dto.SpellDto

import com.machado001.lilol.common.model.data.Skin
import com.machado001.lilol.rotation.model.dto.SkinDto

fun ChampionDto.toChampion(): Champion = Champion(
    key = key,
    name = name,
    id = id,
    image = image.full,
    tags = tags,
    version = version
)

fun DataDragonDto.toDataDragon(): DataDragon = DataDragon(
    data = data.mapValues { it.value.toChampion() },
    version = version
)

fun RotationsDto.toRotations() = Rotations(
    freeChampionIds = freeChampionIds,
    freeChampionIdsForNewPlayers = freeChampionIdsForNewPlayers,
    maxNewPlayerLevel = maxNewPlayerLevel
)

fun SpecificChampionDto.toChampionDetails(): ChampionDetails = with(data.values.first()) {
    ChampionDetails(
        name = name,
        id = id, //champion name to fetch image
        lore = lore,
        title = title,
        image = id,
        tags = tags,
        key = key,
        allytips = allytips,
        enemytips = enemytips,
        spells = spells.map { it.toSpell() },
        skins = skins.map { it.toSkin() }
    )
}

fun SpellDto.toSpell(): Spell = Spell(
    id = id,
    name = name,
    description = description,
    image = image.full
)

fun SkinDto.toSkin(): Skin = Skin(
    id = id,
    num = num,
    name = name,
    chromas = chromas
)

val Any.TAG: String get() = this.javaClass.simpleName