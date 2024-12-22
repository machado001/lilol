package com.machado001.lilol.common.background

import com.machado001.lilol.rotation.model.repository.SettingsRepository

class DummySettingsRepository : SettingsRepository {
    override fun getAppLanguage(): String {
        return ""
    }

    override suspend fun getApiLanguages(): List<String> {
        return emptyList()
    }

}
