package com.machado001.lilol.rotation.view.fragment

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.view.get
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.machado001.lilol.Application
import com.machado001.lilol.R
import com.machado001.lilol.common.Constants
import com.machado001.lilol.common.ListChampionPair
import com.machado001.lilol.common.model.data.Skin
import com.machado001.lilol.common.view.PicassoGradientTransformation
import com.machado001.lilol.common.view.SpellListItem
import com.machado001.lilol.common.view.fadeOutAndGone
import com.machado001.lilol.common.view.startSkeletonPulse
import com.machado001.lilol.common.view.stopSkeletonPulse
import com.machado001.lilol.databinding.FragmentChampionDetailRemakeTabBinding
import com.machado001.lilol.rotation.ChampionDetails
import com.machado001.lilol.rotation.presentation.ChampionDetailsPresenter
import com.machado001.lilol.rotation.view.adapter.RotationAdapter
import com.machado001.lilol.rotation.view.adapter.SpellsAdapter
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.util.Locale
import com.machado001.lilol.rotation.view.fragment.TipsBottomSheetFragment.Companion.TAG as TipsTag


class ChampionDetailsFragment : Fragment(R.layout.fragment_champion_detail_remake_tab),
    ChampionDetails.View {

    private var binding: FragmentChampionDetailRemakeTabBinding? = null
    override lateinit var presenter: ChampionDetails.Presenter
    private var championSkins: List<Skin> = emptyList()
    private var currentChampionId: String = ""
    private var allyTipsContent: List<String> = emptyList()
    private var enemyTipsContent: List<String> = emptyList()
    private var currentChampionName: String = ""
    private var currentChampionLore: String = ""
    private val args: ChampionDetailsFragmentArgs by navArgs()
    private var toolbarMenuVisibility: Map<Int, Boolean>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()

        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds =
            setOf(
                R.id.allChampionsFragment,
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
            skinsCard.setOnClickListener {
                if (championSkins.isNotEmpty()) {
                    showSkinsBottomSheet()
                }
            }
            val onSurface =
                MaterialColors.getColor(detailsToolbar, com.google.android.material.R.attr.colorOnSurface)
            detailsToolbar.setTitleTextColor(onSurface)
            detailsToolbar.navigationIcon?.setTint(onSurface)
            detailsToolbar.overflowIcon?.setTint(onSurface)
            for (index in 0 until detailsToolbar.menu.size) {
                detailsToolbar.menu[index].icon?.setTint(onSurface)
            }
            detailsToolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.allChampionsFragment -> {
                        if (!navController.popBackStack(R.id.allChampionsFragment, false)) {
                            navController.navigate(
                                R.id.allChampionsFragment,
                                null,
                                NavOptions.Builder()
                                    .setLaunchSingleTop(true)
                                    .build()
                            )
                        }
                        true
                    }
                    R.id.rotationFragment -> {
                        if (!navController.popBackStack(R.id.rotationFragment, false)) {
                            navController.navigate(
                                R.id.rotationFragment,
                                null,
                                NavOptions.Builder()
                                    .setLaunchSingleTop(true)
                                    .build()
                            )
                        }
                        true
                    }
                    R.id.settingsFragment -> {
                        if (!navController.popBackStack(R.id.settingsFragment, false)) {
                            navController.navigate(
                                R.id.settingsFragment,
                                null,
                                NavOptions.Builder()
                                    .setLaunchSingleTop(true)
                                    .build()
                            )
                        }
                        true
                    }
                    else -> NavigationUI.onNavDestinationSelected(item, navController)
                }
            }
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

    fun setToolbarOpaqueForInterstitial(showing: Boolean) {
        val detailsBinding = binding ?: return
        val toolbar = detailsBinding.detailsToolbar
        val collapsing = detailsBinding.detailsCollapsing
        if (toolbarMenuVisibility == null) {
            toolbarMenuVisibility =
                (0 until toolbar.menu.size).associate { index ->
                    val item = toolbar.menu.getItem(index)
                    item.itemId to item.isVisible
                }
        }
        if (showing) {
            val surface = MaterialColors.getColor(
                toolbar,
                com.google.android.material.R.attr.colorSurface
            )
            toolbar.setBackgroundColor(surface)
            collapsing.statusBarScrim = ColorDrawable(surface)
            toolbar.navigationIcon?.alpha = 0
            for (index in 0 until toolbar.menu.size) {
                toolbar.menu.getItem(index).apply {
                    isVisible = false
                    isEnabled = false
                    icon?.alpha = 0
                }
            }
            toolbar.isEnabled = false
        } else {
            toolbar.setBackgroundColor(Color.TRANSPARENT)
            collapsing.statusBarScrim = ColorDrawable(Color.TRANSPARENT)
            toolbar.navigationIcon?.alpha = 255
            for (index in 0 until toolbar.menu.size) {
                toolbar.menu.getItem(index).apply {
                    isVisible = toolbarMenuVisibility?.get(itemId) ?: true
                    isEnabled = true
                    icon?.alpha = 255
                }
            }
            toolbar.isEnabled = true
        }
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
            detailsLoadingPlaceholder.stopSkeletonPulse()
            detailsLoadingPlaceholder.visibility = View.GONE
            detailsImagePlaceholder.stopSkeletonPulse()
            detailsImagePlaceholder.visibility = View.GONE
            imageDetailsChampionImage.visibility = View.INVISIBLE
            constraintDetailsChampion.visibility = View.GONE
            textDetailsChampionName.text = getString(R.string.error_request)
            textDetailsChampionTitle.text = ""
            textDetailsChampionLore.text = ""
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
                    currentChampionName = name
                    currentChampionLore = lore
                    allyTipsContent = buildTipsList(
                        allytips,
                        lore,
                        getString(R.string.no_available_tips_for_playing_with_champion)
                    )
                    enemyTipsContent = buildTipsList(
                        enemytips,
                        lore,
                        getString(R.string.no_available_tips_for_playing_against_champion)
                    )

                    textDetailsChampionName.text = name
                    textDetailsChampionTitle.text = title
                    textDetailsChampionLore.text = lore
                    val rolesText = tags.joinToString(", ")
                    textDetailsChampionRoles.text = rolesText
                    textDetailsChampionRoles.visibility =
                        if (rolesText.isNotBlank()) View.VISIBLE else View.GONE

                    detailsCollapsing.apply {
                        title = name
                        setExpandedTitleColor(Color.TRANSPARENT)
                    }

                    textDetailsChampionAllyTitle.apply {
                        text = getString(R.string.playing_with_champion, name)
                    }
                    textDetailsChampionEnemyTitle.apply {
                        text = getString(R.string.playing_against_champion, name)
                    }
                }
                loreCard.setOnClickListener {
                    openTipsBottomSheet(
                        getString(R.string.lore),
                        listOf(currentChampionLore)
                    )
                }
                allyCard.setOnClickListener {
                    openTipsBottomSheet(
                        getString(R.string.playing_with_champion, currentChampionName),
                        allyTipsContent
                    )
                }
                enemyCard.setOnClickListener {
                    openTipsBottomSheet(
                        getString(R.string.playing_against_champion, currentChampionName),
                        enemyTipsContent
                    )
                }
            }
        }
    }


    override fun showProgress(show: Boolean) {
        binding?.apply {
            if (show) {
                detailsLoadingPlaceholder.animate().cancel()
                detailsLoadingPlaceholder.alpha = 1f
                detailsLoadingPlaceholder.visibility = View.VISIBLE
                detailsLoadingPlaceholder.startSkeletonPulse()
                detailsImagePlaceholder.animate().cancel()
                detailsImagePlaceholder.alpha = 1f
                detailsImagePlaceholder.visibility = View.VISIBLE
                detailsImagePlaceholder.startSkeletonPulse()
                imageDetailsChampionImage.visibility = View.INVISIBLE
            } else {
                detailsLoadingPlaceholder.stopSkeletonPulse()
                detailsLoadingPlaceholder.fadeOutAndGone()
                detailsImagePlaceholder.stopSkeletonPulse()
                detailsImagePlaceholder.fadeOutAndGone()
                imageDetailsChampionImage.visibility = View.VISIBLE
            }
            constraintDetailsChampion.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    override fun showSpellList(spells: List<SpellListItem>) {
        binding?.apply {
            rvSkills.apply {
                adapter = SpellsAdapter(spells) { spell ->
                    openSpellDetail(spell)
                }
                layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            }
        }
    }

    override fun showSkinsList(skins: List<Skin>, championId: String) {
        this.championSkins = skins
        this.currentChampionId = championId
    }

    private fun buildTipsList(
        tips: List<String>,
        lore: String,
        noTipsMessage: String,
    ): List<String> {
        if (tips.isEmpty() || tips.contains(lore)) {
            return listOf(noTipsMessage)
        }
        return tips.map { "- $it" }
    }

    private fun openTipsBottomSheet(title: String, tips: List<String>) {
        if (tips.isEmpty()) return
        val currentSheet = childFragmentManager.findFragmentByTag(TipsTag)
        if (currentSheet != null && currentSheet.isVisible) return
        val bottomSheet = TipsBottomSheetFragment.newInstance(ArrayList(tips), title)
        bottomSheet.show(childFragmentManager, TipsTag)
    }

    private fun openSpellDetail(spell: SpellListItem) {
        val sheet = SpellDetailBottomSheetFragment.newInstance(spell)
        sheet.show(childFragmentManager, SpellDetailBottomSheetFragment.TAG)
    }

    private fun showSkinsBottomSheet() {
        val currentSheet = childFragmentManager.findFragmentByTag(SkinsBottomSheetFragment.TAG)
        if (currentSheet != null && currentSheet.isVisible) return
        val bottomSheet =
            SkinsBottomSheetFragment.newInstance(ArrayList(championSkins), currentChampionId)
        bottomSheet.show(childFragmentManager, SkinsBottomSheetFragment.TAG)
    }

}
