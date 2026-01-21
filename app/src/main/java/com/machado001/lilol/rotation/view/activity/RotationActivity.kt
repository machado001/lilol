package com.machado001.lilol.rotation.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.machado001.lilol.R
import com.machado001.lilol.ads.AdsConfig
import com.machado001.lilol.ads.InterstitialAdManager
import com.machado001.lilol.databinding.ActivityRotationBinding
import kotlinx.coroutines.launch
import java.util.Locale

class RotationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRotationBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    
    private val visitedChampionNames = mutableSetOf<String>()
    private val countedEntryKey = "champion_detail_counted"

    override fun attachBaseContext(newBase: Context) {
        val langPref = PreferenceManager.getDefaultSharedPreferences(newBase)
        val defaultLocale = Locale.getDefault()
        val lang = langPref.getString("appLanguage", defaultLocale.toString()) ?: defaultLocale.toString()
        val (code, country) = if (lang.contains("_")) {
            lang.split("_")
        } else {
            listOf(lang, "")
        }
        val locale = if (country.isNotEmpty()) Locale(code, country) else Locale(code)
        
        val config = newBase.resources.configuration
        Locale.setDefault(locale)
        config.setLocale(locale)
        
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityRotationBinding.inflate(layoutInflater)
        configureWindowInsets(binding.root)
        setContentView(binding.root)
        
        enforceLocale()

        lifecycleScope.launch {
            InterstitialAdManager.preload(this@RotationActivity)
            enforceLocale()
        }
        
        if (AdsConfig.checkAndConsumePendingAdOnRestart(this)) {
            binding.root.postDelayed({
                enforceLocale()
                
                InterstitialAdManager.showIfAvailable(this) {
                    enforceLocale()
                }
            }, 1000)
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.bottomNav.apply {
            setupWithNavController(navController)

            navController.addOnDestinationChangedListener { _, destination, arguments ->
                visibility = if (destination.id == R.id.championDetailFragment) {
                    View.GONE
                } else {
                    View.VISIBLE
                }

                if (destination.id == R.id.championDetailFragment) {
                    val entry = navController.currentBackStackEntry
                    val alreadyCounted = entry?.savedStateHandle?.get<Boolean>(countedEntryKey) == true
                    if (!alreadyCounted) {
                        entry?.savedStateHandle?.set(countedEntryKey, true)
                        val championName = arguments?.getString("championName")
                        if (!championName.isNullOrBlank()) {
                            val isNewVisit = visitedChampionNames.add(championName)
                            if (isNewVisit && visitedChampionNames.size % 3 == 0) {
                                enforceLocale()
                                InterstitialAdManager.showIfAvailable(this@RotationActivity) {
                                    enforceLocale()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Helper to enforce the user's selected locale.
     * This combats the "WebView Locale Reset" bug often caused by AdMob/WebView initialization.
     */
    private fun enforceLocale() {
        val langPref = PreferenceManager.getDefaultSharedPreferences(this)
        val lang = langPref.getString("appLanguage", "") ?: ""
        
        if (lang.isNotEmpty()) {
            val (code, country) = if (lang.contains("_")) {
                lang.split("_")
            } else {
                listOf(lang, "")
            }
            val targetLocale = if (country.isNotEmpty()) Locale(code, country) else Locale(code)
            
            if (Locale.getDefault() != targetLocale) {
                Locale.setDefault(targetLocale)
                val config = resources.configuration
                config.setLocale(targetLocale)
                resources.updateConfiguration(config, resources.displayMetrics)
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
}
