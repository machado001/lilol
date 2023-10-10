package com.machado001.lilol.rotation.model.local

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.machado001.lilol.common.Constants.KEY_LANGUAGE_PREF
import java.util.Locale

class SettingsLocalDataSourceImpl(context: Context) : SettingsLocalDataSource {

    private val defaultPreferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }
    private val deviceAppLanguage: String = Locale.getDefault().toString()
    override fun getAppLanguage() =
        defaultPreferences.getString(KEY_LANGUAGE_PREF, deviceAppLanguage)!!

}


