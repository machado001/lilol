package com.machado001.lilol.rotation.view.fragment


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
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
    private var pagerAdapter: RotationPagerAdapter? = null
    private var tabMediator: TabLayoutMediator? = null
    private var hasError: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        val championsManager = (activity?.application as Application)
            .container.championsManager


        binding = FragmentRotationBinding.bind(view)
        presenter = RotationPresenter(championsManager, this)

        binding?.freeWeekToolbar?.setupWithNavController(navController, appBarConfiguration)
        setupTabsAndPager()

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
        binding?.apply {
            if (enabled) hasError = false
            progressRotation.visibility = if (enabled) View.VISIBLE else View.GONE
            if (hasError && !enabled) {
                // Remain in error state
                return
            }
            val contentVisibility = if (enabled) View.GONE else View.VISIBLE
            rotationTabs.visibility = contentVisibility
            rotationViewpager.visibility = contentVisibility
            freeWeekToolbar.visibility = contentVisibility
            textErrorRequest.visibility = View.GONE
        }
    }

    override fun showFailureMessage() {
        hasError = true
        binding?.apply {
            progressRotation.visibility = View.GONE
            rotationTabs.visibility = View.GONE
            rotationViewpager.visibility = View.GONE
            textErrorRequest.apply {
                visibility = View.VISIBLE
                text = getString(R.string.error_request)
            }
            freeWeekToolbar.visibility = View.GONE
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
                pagerAdapter?.submitData(
                    listOf(
                        freeChampionForNewPlayersMap,
                        freeChampionsMap
                    )
                )
                rotationTabs.getTabAt(0)?.text =
                    getString(
                        R.string.new_player_range,
                        level,
                        freeChampionForNewPlayersMap.size
                    )
                rotationTabs.getTabAt(1)?.text =
                    getString(
                        R.string.normal_player_range,
                        level,
                        freeChampionsMap.size
                    )
            }
        }
    }

    private fun setupTabsAndPager() {
        val binding = binding ?: return
        pagerAdapter = RotationPagerAdapter { key, name, version ->
            goToChampionDetailsScreen(key, name, version)
        }
        binding.rotationViewpager.adapter = pagerAdapter
        tabMediator = TabLayoutMediator(binding.rotationTabs, binding.rotationViewpager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.week_rotation_two, "—")
                else -> getString(R.string.free_week)
            }
        }.also { it.attach() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator?.detach()
        tabMediator = null
        pagerAdapter = null
    }

    private inner class RotationPagerAdapter(
        private val onClick: (String, String, String) -> Unit,
    ) : RecyclerView.Adapter<RotationPagerAdapter.PageViewHolder>() {

        private var pages: List<ListChampionPair> = emptyList()

        fun submitData(data: List<ListChampionPair>) {
            pages = data
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_rotation_page, parent, false)
            return PageViewHolder(view as RecyclerView)
        }

        override fun getItemCount(): Int = pages.size

        override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
            holder.bind(pages[position])
        }

        inner class PageViewHolder(
            private val recyclerView: RecyclerView,
        ) : RecyclerView.ViewHolder(recyclerView) {
            fun bind(champions: ListChampionPair) {
                val spanCount =
                    if (recyclerView.resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) 4 else 7
                recyclerView.layoutManager = GridLayoutManager(recyclerView.context, spanCount)
                recyclerView.adapter =
                    RotationAdapter(champions) { championKey, championName, championVersion ->
                        onClick(championKey, championName, championVersion)
                    }
            }
        }
    }
}
