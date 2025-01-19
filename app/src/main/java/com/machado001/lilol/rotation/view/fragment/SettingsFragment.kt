package com.machado001.lilol.rotation.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.machado001.lilol.Application
import com.machado001.lilol.R
import com.machado001.lilol.rotation.Settings
import com.machado001.lilol.rotation.presentation.SettingsPresenter
import kotlinx.coroutines.launch
import java.util.Locale

class SettingsFragment : PreferenceFragmentCompat(), Settings.View {

    override lateinit var presenter: Settings.Presenter
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val repositoryImpl = (activity?.application as Application).container.settingsRepository

        viewLifecycleOwnerLiveData.observe(this) { lifecycleOwner ->
            presenter = SettingsPresenter(this, repositoryImpl)
            lifecycleOwner.lifecycleScope.launch {
                presenter.changeAppLanguage()
            }
        }
    }

    override fun displayLanguageOptions(
        readableLanguages: List<String>,
        availableLanguages: List<String>,
    ) {
        findPreference<ListPreference>("appLanguage")?.apply {
            summary = Locale.getDefault().displayName
            entryValues = availableLanguages.toTypedArray()
            entries = readableLanguages.toTypedArray()
            onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val (code, country) = newValue.toString().split("_")
                    val newLocaleValue = Locale(code, country)
                    val displayName = newLocaleValue.displayName

                    AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.change_language, displayName))
                        .setMessage(getString(R.string.change_language_confirm))
                        .setPositiveButton(getString(R.string.yes)) { _, _ ->
                            val intent =
                                requireActivity().intent
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            requireActivity().finish()
                            startActivity(intent)
                        }
                        .setNegativeButton(getString(R.string.no), null)
                        .show()
                    true // Return false to prevent immediate change
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDestroy()
    }
}



