package com.machado001.lilol.rotation.view.fragment

import android.animation.LayoutTransition
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import android.widget.TextView
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
import com.machado001.lilol.common.view.PicassoGradientTransformation
import com.machado001.lilol.common.view.SpellListItem
import com.machado001.lilol.databinding.FragmentChampionDetailRemakeTabBinding
import com.machado001.lilol.rotation.ChampionDetails
import com.machado001.lilol.rotation.presentation.ChampionDetailsPresenter
import com.machado001.lilol.rotation.view.adapter.RotationAdapter
import com.machado001.lilol.rotation.view.adapter.SpellsAdapter
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

                    textDetailsChampionAllyTitle.apply {
                        text = getString(R.string.playing_with_champion, name)
                    }
                    textDetailsChampionEnemyTitle.apply {
                        text = getString(R.string.playing_against_champion, name)
                    }

                    linearLayoutDetailsChampionAllyTips.apply {

                        if (allytips.isEmpty() || allytips.contains(lore)) {
                            val textView = TextView(
                                requireContext(),
                                null,
                                0,
                                R.style.Theme_Lilol_TextViewBase_ChampionDetail,
                            ).apply {
                                text =
                                    getString(R.string.no_available_tips_for_playing_with_champion)
                                textSize = 16.0f
                            }

                            addView(textView)
                        } else {
                            allytips.forEach { allyTip ->
                                val textView = TextView(
                                    requireContext(),
                                    null,
                                    0,
                                    R.style.Theme_Lilol_TextViewBase_ChampionDetail,
                                ).apply {
                                    text = allyTip
                                }
                                val space = Space(requireContext()).apply {
                                    layoutParams = ViewGroup.LayoutParams(0, 16)
                                }
                                addView(textView)
                                if (allyTip != allytips.last()) addView(space)
                            }

                        }

                    }

                    linearLayoutDetailsChampionEnemyTips.apply {

                        if (enemytips.isEmpty() || enemytips.contains(lore)) {
                            val textView = TextView(
                                requireContext(),
                                null,
                                0,
                                R.style.Theme_Lilol_TextViewBase_ChampionDetail,
                            ).apply {
                                text =
                                    getString(R.string.no_available_tips_for_playing_against_champion)
                                textSize = 16.0f
                            }

                            addView(textView)
                        } else {
                            enemytips.forEach { enemyTip ->
                                val textView = TextView(
                                    requireContext(),
                                    null,
                                    0,
                                    R.style.Theme_Lilol_TextViewBase_ChampionDetail,
                                ).apply {
                                    text = enemyTip
                                }
                                val space = Space(requireContext()).apply {
                                    layoutParams = ViewGroup.LayoutParams(0, 16)
                                }
                                addView(textView)
                                if (enemyTip != enemytips.last()) addView(space)
                            }
                        }
                    }
                }
                val sectionClickListeners = arrayOf(
                    loreCard to { textDetailsChampionLore },
                    allyCard to { linearLayoutDetailsChampionAllyTips },
                    enemyCard to { linearLayoutDetailsChampionEnemyTips }
                )

                val sectionStates = booleanArrayOf(false, false, false)

                sectionClickListeners.forEachIndexed { index, (card, content) ->
                    card.apply {
                        setOnClickListener {
                            sectionStates[index] = !sectionStates[index]
                            layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

                            content().apply {
                                visibility = when (sectionStates[index]) {
                                    true -> View.VISIBLE
                                    false -> View.GONE
                                }
                            }
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

    override fun showSpellList(spells: List<SpellListItem>) {
        binding?.apply {
            rvSkills.apply {
                adapter = SpellsAdapter(spells, args.championVersion)
                layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            }
        }
    }
}




