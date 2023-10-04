package com.machado001.lilol.common.di

import com.machado001.lilol.rotation.model.network.HTTPService
import com.machado001.lilol.rotation.model.repository.ChampionRepository
import com.machado001.lilol.rotation.model.repository.DataDragonRepositoryImpl
import com.machado001.lilol.rotation.model.repository.RotationRepositoryImpl

object DependencyInjector {
    fun championRepository(): ChampionRepository {
        return ChampionRepository(
            DataDragonRepositoryImpl(HTTPService.championApi),
            RotationRepositoryImpl(HTTPService.rotationApi)

        )
    }
}