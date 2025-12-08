package com.machado001.lilol.rotation.view.fragment

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.machado001.lilol.Application
import com.machado001.lilol.R
import com.machado001.lilol.common.Constants
import com.machado001.lilol.common.ListChampionPair
import com.machado001.lilol.common.model.data.Skin
import com.machado001.lilol.common.view.PicassoGradientTransformation
import com.machado001.lilol.common.view.SpellListItem
import com.machado001.lilol.databinding.FragmentChampionDetailRemakeTabBinding
import com.machado001.lilol.rotation.ChampionDetails
import com.machado001.lilol.rotation.presentation.ChampionDetailsPresenter
import com.machado001.lilol.rotation.view.adapter.RotationAdapter
import com.machado001.lilol.rotation.view.adapter.SpellsAdapter
import com.machado001.lilol.rotation.view.fragment.SkinsBottomSheetFragment
import com.machado001.lilol.rotation.view.fragment.SpellDetailBottomSheetFragment
import com.machado001.lilol.rotation.view.fragment.TipsBottomSheetFragment
import com.machado001.lilol.rotation.view.fragment.TipsBottomSheetFragment.Companion.TAG as TipsTag
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.util.Locale


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
            skinsCard.setOnClickListener {
                if (championSkins.isNotEmpty()) {
                    showSkinsBottomSheet()
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
        binding?.let { it ->
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
            progressBar.visibility = if (show) View.VISIBLE else View.GONE
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
