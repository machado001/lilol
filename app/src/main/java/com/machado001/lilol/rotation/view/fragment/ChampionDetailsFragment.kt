package com.machado001.lilol.rotation.view.fragment

import android.animation.LayoutTransition
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.machado001.lilol.Application
import com.machado001.lilol.R
import com.machado001.lilol.common.Constants
import com.machado001.lilol.common.ListChampionPair
import com.machado001.lilol.common.view.PicassoGradientTransformation
import com.machado001.lilol.databinding.FragmentChampionDetailRemakeTabBinding
import com.machado001.lilol.rotation.ChampionDetails
import com.machado001.lilol.rotation.presentation.ChampionDetailsPresenter
import com.machado001.lilol.rotation.view.adapter.ChampionDetailPagerAdapter
import com.machado001.lilol.rotation.view.adapter.RotationAdapter
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.util.Locale


class ChampionDetailsFragment : Fragment(R.layout.fragment_champion_detail_remake_tab),
    ChampionDetails.View {

    private var binding: FragmentChampionDetailRemakeTabBinding? = null
    override lateinit var presenter: ChampionDetails.Presenter
    private val args: ChampionDetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()

        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds =
            setOf(
                R.id.homeFragment,
                R.id.settingsFragment
            )
        )

        val repositoryImpl =
            (activity?.application as Application).container.dataDragonRepository
        presenter = ChampionDetailsPresenter(repositoryImpl, this)
        binding = FragmentChampionDetailRemakeTabBinding.bind(view)

        binding?.apply {
            detailsCollapsing.apply {
                setupWithNavController(detailsToolbar, navController, appBarConfiguration)
                detailsToolbar.popupTheme = R.style.Theme_Lilol
            }

            championPager.adapter = ChampionDetailPagerAdapter(this@ChampionDetailsFragment)
            championPager.isUserInputEnabled = false

            TabLayoutMediator(championTab, championPager) { tab, position ->
                when (position) {
                    0 -> tab.text = "Skills"
                    1 -> tab.text = "Lore"
                    2 -> tab.text = "Skins"
                    3 -> tab.text = "Related Champions"
                }
            }.attach()

        }

        viewLifecycleOwner.lifecycleScope.launch {
            presenter.getChampionDetails(
                args.championVersion,
                Locale.getDefault().toString(),
                args.championName
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        presenter.onDestroy()
    }


    override fun setupRecyclerView(champions: ListChampionPair) {
        val spanCount =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 4 else 6
        binding?.rvRelatedChampions?.apply {
            adapter = RotationAdapter(champions) { championId, championName, championVersion ->
                goToChampionDetailsScreen(championId, championName, championVersion)
            }
            layoutManager = GridLayoutManager(requireContext(), spanCount)

        }
    }


    private fun goToChampionDetailsScreen(
        championId: String,
        championNameKey: String,
        championVersion: String,
    ) {
        val action =
            ChampionDetailsFragmentDirections.actionChampionDetailFragmentSelf(
                championId,
                championNameKey,
                championVersion
            )
        findNavController().navigate(action)
    }

    override fun showSuccess(champions: ListChampionPair) {
        setupRecyclerView(champions)
    }

    override fun showErrorMessage() {
        binding?.apply {
        }
    }

    override fun goToMoreDetailsScreen() {
    }

    override fun setupChampionDetails(championDetail: com.machado001.lilol.common.model.data.ChampionDetails) {
        binding?.let {
            with(it) {
                Picasso.get()
                    .load("${Constants.DATA_DRAGON_BASE_URL}cdn/img/champion/splash/${championDetail.id}_0.jpg")
                    .transform(PicassoGradientTransformation)
                    .into(imageDetailsChampionImage)

                championDetail.apply {
                    textDetailsChampionName.text = name
                    textDetailsChampionTitle.text = title
                    textDetailsChampionLore.text = lore

                    detailsCollapsing.apply {
                        title = name
                        setExpandedTitleColor(Color.TRANSPARENT)
                    }

                }

                var isExpanded = false

                loreCard.apply {
                    setOnClickListener {
                        isExpanded = !isExpanded
                        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

                        textDetailsChampionLore.apply {
                            visibility = when (isExpanded) {
                                true -> View.VISIBLE
                                false -> View.GONE
                            }
                        }

                        constraintCardWrapper.apply {
                        }
                    }
                }
            }
        }
    }

    override fun showProgress(show: Boolean) {
        binding?.apply {
            progressBar.visibility = if (show) View.VISIBLE else View.GONE
            constraintDetailsChampion.visibility = if (show) View.GONE else View.VISIBLE

        }
    }
}



