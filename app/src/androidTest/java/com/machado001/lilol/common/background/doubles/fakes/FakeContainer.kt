package com.machado001.lilol.common.background.doubles.fakes

import com.machado001.lilol.common.background.doubles.dummies.DummyChampionsManager
import com.machado001.lilol.common.background.doubles.dummies.DummyDataDragonRepository
import com.machado001.lilol.common.background.doubles.dummies.DummySettingsRepository
import com.machado001.lilol.common.di.Container
import com.machado001.lilol.rotation.model.repository.ChampionsManager
import com.machado001.lilol.rotation.model.repository.DataDragonRepository
import com.machado001.lilol.rotation.model.repository.RotationRepository
import com.machado001.lilol.rotation.model.repository.SettingsRepository



class FakeContainer : Container {
    override val rotationRepository: RotationRepository
        get() = FakeRotationRepository()

    override val championsManager: ChampionsManager
        get() = DummyChampionsManager()

    override val dataDragonRepository: DataDragonRepository
        get() = DummyDataDragonRepository()

    override val settingsRepository: SettingsRepository
        get() = DummySettingsRepository()
}