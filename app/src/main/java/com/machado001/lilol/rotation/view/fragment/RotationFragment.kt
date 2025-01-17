package com.machado001.lilol.rotation.view.fragment


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.machado001.lilol.Application
import com.machado001.lilol.R
import com.machado001.lilol.common.ListChampionPair
import com.machado001.lilol.databinding.FragmentRotationBinding
import com.machado001.lilol.rotation.Rotation
import com.machado001.lilol.rotation.presentation.RotationPresenter
import com.machado001.lilol.rotation.view.adapter.RotationAdapter
import kotlinx.coroutines.launch

class RotationFragment : Fragment(R.layout.fragment_rotation), Rotation.View {

    override lateinit var presenter: Rotation.Presenter
    private var binding: FragmentRotationBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        val championsManager = (activity?.application as Application)
            .container.championsManager


        binding = FragmentRotationBinding.bind(view)
        presenter = RotationPresenter(championsManager, this)

        binding?.freeWeekToolbar?.setupWithNavController(navController, appBarConfiguration)

        viewLifecycleOwner.lifecycleScope.launch {
            presenter.displayRotations()
            askNotificationPermission()
        }

    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { _: Boolean -> }


    @SuppressLint("InlinedApi")
    private fun askNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("If you want to receive new rotations in the future, please, allow notifications permission. ")
                .setPositiveButton(
                    "OK"
                ) { _, _ ->
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                .setNegativeButton(
                    "NOT NOW"
                ) { _, _ -> Unit }
                .show()
        }
    }

    override fun showProgress(enabled: Boolean) {
        binding?.progressRotation?.visibility = if (enabled) View.VISIBLE else View.GONE
    }

    override fun showFailureMessage() {
        binding?.let {
            with(it) {
                textErrorRequest.apply {
                    visibility = View.VISIBLE
                }
            }
        }
    }

    override fun goToChampionDetailsScreen(
        championId: String,
        championName: String,
        championVersion: String,
    ) {
        val action =
            RotationFragmentDirections.actionRotationFragmentToChampionDetailFragment(
                championId,
                championName,
                championVersion
            )
        findNavController().navigate(action)
    }

    override fun showSuccess(
        freeChampionsMap: ListChampionPair,
        freeChampionForNewPlayersMap: ListChampionPair,
        level: Int,
    ) {
        binding?.let {
            with(it) {
                rvRotationMain.apply {
                    adapter =
                        RotationAdapter(freeChampionsMap) { championKey, championName, championVersion ->
                            goToChampionDetailsScreen(
                                championKey,
                                championName,
                                championVersion
                            )
                        }
                    layoutManager =
                        GridLayoutManager(requireContext(), 5)
                }
                rvRotationNewPlayers.apply {
                    adapter =
                        RotationAdapter(freeChampionForNewPlayersMap) { championKey, championName, championVersion ->
                            goToChampionDetailsScreen(
                                championKey,
                                championName,
                                championVersion
                            )
                        }
                    layoutManager =
                        GridLayoutManager(requireContext(), 5)

                }

                newPlayerRange.apply {
                    visibility = View.VISIBLE
                    text =
                        getString(
                            R.string.new_player_range,
                            level,
                            freeChampionForNewPlayersMap.size
                        )
                }
                normalPlayerRange.apply {
                    visibility = View.VISIBLE
                    text =
                        getString(
                            R.string.normal_player_range,
                            level,
                            freeChampionsMap.size
                        )
                }
            }
        }
    }
}
