package com.machado001.lilol.rotation.view

import com.machado001.lilol.common.base.BasePresenter

interface Settings {

    interface Presenter : BasePresenter {
        suspend fun changeAppLanguage()
    }

    interface View {
        fun displayLanguageOptions(
            readableLanguages: List<String>,
            availableLanguages: List<String>
        )
    }

}