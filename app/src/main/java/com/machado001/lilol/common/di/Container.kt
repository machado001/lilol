package com.machado001.lilol.common.di

import com.machado001.lilol.rotation.model.repository.ChampionsManager
import com.machado001.lilol.rotation.model.repository.DataDragonRepository
import com.machado001.lilol.rotation.model.repository.RotationRepository
import com.machado001.lilol.rotation.model.repository.SettingsRepository

interface Container {
    val rotationRepository: RotationRepository
    val championsManager: ChampionsManager
    val dataDragonRepository: DataDragonRepository
    val settingsRepository: SettingsRepository
}
