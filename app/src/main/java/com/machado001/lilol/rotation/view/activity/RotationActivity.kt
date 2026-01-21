package com.machado001.lilol.rotation.view.activity

import android.annotation.SuppressLint
import android.app.Activity
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
import com.machado001.lilol.Application
import com.machado001.lilol.R
import com.machado001.lilol.databinding.ActivityRotationBinding
import com.machado001.lilol.review.Review
import com.machado001.lilol.review.presentation.ReviewPresenter
import kotlinx.coroutines.launch
import java.util.Locale

class RotationActivity : AppCompatActivity(), Review.View {
    private lateinit var binding: ActivityRotationBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    override lateinit var presenter: Review.Presenter

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
        // Locale setup is now handled in attachBaseContext
        binding = ActivityRotationBinding.inflate(layoutInflater)
        configureWindowInsets(binding.root)
        setContentView(binding.root)

        val appContainer = (application as Application).container
        presenter = ReviewPresenter(this, appContainer.reviewManager)
        if (savedInstanceState == null) {
            lifecycleScope.launch {
                presenter.maybePromptForReview()
            }
        }

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

    override fun getReviewActivity(): Activity = this

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

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
