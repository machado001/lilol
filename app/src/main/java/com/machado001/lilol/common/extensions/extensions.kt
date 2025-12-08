package com.machado001.lilol.common.extensions

import com.machado001.lilol.common.model.data.Champion
import com.machado001.lilol.common.model.data.ChampionDetails
import com.machado001.lilol.common.model.data.DataDragon
import com.machado001.lilol.common.Constants
import com.machado001.lilol.common.model.data.Passive
import com.machado001.lilol.common.model.data.Spell
import com.machado001.lilol.rotation.model.dto.ChampionDto
import com.machado001.lilol.rotation.model.dto.DataDragonDto
import com.machado001.lilol.rotation.model.dto.Rotations
import com.machado001.lilol.rotation.model.dto.RotationsDto
import com.machado001.lilol.rotation.model.dto.SpecificChampionDto
import com.machado001.lilol.rotation.model.dto.SpellDto

import com.machado001.lilol.common.model.data.Skin
import com.machado001.lilol.rotation.model.dto.PassiveDto
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

fun SpecificChampionDto.toChampionDetails(version: String): ChampionDetails = with(data.values.first()) {
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
        passive = passive?.toPassive(version) ?: Passive("", "", ""),
        spells = spells.map { it.toSpell(version) },
        skins = skins.map { it.toSkin() }
    )
}

fun SpellDto.toSpell(version: String): Spell = Spell(
    id = id,
    name = name,
    description = sanitizeSpellText(description),
    iconUrl = "${Constants.DATA_DRAGON_BASE_URL}cdn/$version/img/spell/${image.full}",
    cooldownBurn = cooldownBurn,
    costBurn = costBurn,
    rangeBurn = rangeBurn.takeIf { it.isNotBlank() }
)

fun PassiveDto.toPassive(version: String): Passive = Passive(
    name = name,
    description = sanitizeSpellText(description),
    iconUrl = "${Constants.DATA_DRAGON_BASE_URL}cdn/$version/img/passive/${image.full}"
)

private fun sanitizeSpellText(raw: String): String {
    val withoutTags = raw.replace(Regex("<[^>]*>"), "")
    val withoutVars = withoutTags.replace(Regex("\\{\\{[^}]+\\}\\}"), "")
    val withoutAppend = withoutVars.replace("spellmodifierdescriptionappend", "", ignoreCase = true)
    return withoutAppend.replace("\\s+".toRegex(), " ").trim()
}

fun SkinDto.toSkin(): Skin = Skin(
    id = id,
    num = num,
    name = name,
    chromas = chromas
)

val Any.TAG: String get() = this.javaClass.simpleName
