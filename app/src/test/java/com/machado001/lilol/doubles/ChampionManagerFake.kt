package com.machado001.lilol.doubles

import com.machado001.lilol.rotation.model.dto.DataDragonDto
import com.machado001.lilol.rotation.model.dto.RotationsDto
import com.machado001.lilol.rotation.model.repository.ChampionsManager

object ChampionManagerFake : ChampionsManager {

    private const val VERSION = "13.19.1"

    private val fakeRotationsDto: RotationsDto = RotationsDto(
        freeChampionIds = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10,11,12,13,14,15,16,17,18,19,20),
        freeChampionIdsForNewPlayers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
        maxNewPlayerLevel = 10
    )

    private val fakeSupportedLanguages: List<String> = listOf(
        "it_IT", "fr_FR", "ja_JP", "ko_KR", "pt_BR", "en_US",
    )

    private val fakeDataDragonDto: DataDragonDto = DataDragonDto(
        VERSION,
        "",
    )

    override suspend fun getSupportedLanguages() = fakeSupportedLanguages
    override suspend fun getCurrentVersion() = VERSION
    override suspend fun getDataDragon(language: String): DataDragonDto {
        TODO("Not yet implemented")
    }
    override suspend fun getRotations() = fakeRotationsDto
}