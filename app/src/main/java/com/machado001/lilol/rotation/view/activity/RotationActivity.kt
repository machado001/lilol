package com.machado001.lilol.rotation.view.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.machado001.lilol.R
import com.machado001.lilol.databinding.ActivityRotationBinding
import java.util.Locale

class RotationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRotationBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setupLocalLanguage()
        binding = ActivityRotationBinding.inflate(layoutInflater)
        configureWindowInsets(binding.root)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.bottomNav.apply {
            setupWithNavController(navController)

            navController.addOnDestinationChangedListener { _, destination, _ ->
                visibility = if (destination.id == R.id.championDetailFragment) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }
        }
    }

    private fun configureWindowInsets(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
            windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).apply {
                v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin = top
                    leftMargin = left
                    bottomMargin = bottom
                    rightMargin = right
                }
            }
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setupLocalLanguage() {
        val langPref = PreferenceManager.getDefaultSharedPreferences(this)
        val defaultLocale = Locale.getDefault()
        val lang =
            langPref.getString("appLanguage", defaultLocale.toString()) ?: defaultLocale.toString()
        val (code, country) = lang.split("_")
        val locale = Locale(code, country)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}