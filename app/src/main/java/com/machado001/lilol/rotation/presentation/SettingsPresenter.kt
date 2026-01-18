package com.machado001.lilol.rotation.presentation

import com.machado001.lilol.rotation.Settings
import com.machado001.lilol.rotation.model.repository.SettingsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.Locale

class SettingsPresenter(
    private var view: Settings.View?,
    private var repository: SettingsRepository,
) : Settings.Presenter {

    override suspend fun changeAppLanguage(): Unit = coroutineScope {
        val availableLanguages = repository.getApiLanguages()

        val userReadableLanguages = availableLanguages.map { lang ->
            async {
                val parts = lang.split("_")
                val code = parts.getOrElse(0) { "" }
                val country = parts.getOrElse(1) { "" }
                val locale = if (country.isNotEmpty()) Locale(code, country) else Locale(code)
                locale.displayName
            }
        }.awaitAll()

        view?.displayLanguageOptions(userReadableLanguages, availableLanguages)
    }

    override fun onDestroy() {
        view = null
    }
}
