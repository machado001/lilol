package com.machado001.lilol.rotation.presentation

import com.machado001.lilol.rotation.model.repository.SettingsRepository
import com.machado001.lilol.rotation.Settings
import java.util.Locale

class SettingsPresenter(
    private var view: Settings.View?,
    private var repository: SettingsRepository
) : Settings.Presenter {
    override suspend fun changeAppLanguage() {
//        val appLanguage = repository.getAppLanguage()
        val availableLanguages = repository.getApiLanguages()

        val userReadableLanguages =
            availableLanguages.map {
                val (code, country) = it.split("_")
                Locale(code, country).displayName
            }
        view?.displayLanguageOptions(userReadableLanguages, availableLanguages)
    }

    override fun onDestroy() {
        view = null
    }
}