package com.machado001.lilol.common.di

import com.machado001.lilol.rotation.model.network.HTTPService
import com.machado001.lilol.rotation.model.repository.ChampionsManager
import com.machado001.lilol.rotation.model.repository.ChampionsManagerImpl
import com.machado001.lilol.rotation.model.repository.DataDragonRepository
import com.machado001.lilol.rotation.model.repository.DataDragonRepositoryImpl
import com.machado001.lilol.rotation.model.repository.RotationRepository
import com.machado001.lilol.rotation.model.repository.RotationRepositoryImpl

class AppContainer {

    private val dataDragonRepository: DataDragonRepository by lazy {
        DataDragonRepositoryImpl(HTTPService.dataDragonApi)
    }

    private val rotationRepository: RotationRepository by lazy {
        RotationRepositoryImpl(HTTPService.rotationApi)
    }

    val championsManager: ChampionsManager by lazy {
        ChampionsManagerImpl(dataDragonRepository, rotationRepository)
    }

}
