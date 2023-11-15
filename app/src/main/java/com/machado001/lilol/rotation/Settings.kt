package com.machado001.lilol.rotation

import com.machado001.lilol.common.base.BasePresenter
import com.machado001.lilol.common.base.BaseView

interface Settings {

    interface Presenter : BasePresenter {
        suspend fun changeAppLanguage()
    }

    interface View : BaseView<Presenter> {
        fun displayLanguageOptions(
            readableLanguages: List<String>,
            availableLanguages: List<String>,
        )
    }

}