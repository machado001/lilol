package com.machado001.lilol.rotation.view.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.machado001.lilol.Application
import com.machado001.lilol.R
import com.machado001.lilol.common.Constants
import com.machado001.lilol.common.ListChampionPair
import com.machado001.lilol.common.view.PicassoGradientTransformation
import com.machado001.lilol.databinding.FragmentChampionDetailBinding
import com.machado001.lilol.rotation.ChampionDetails
import com.machado001.lilol.rotation.presentation.ChampionDetailsPresenter
import com.machado001.lilol.rotation.view.adapter.RotationAdapter
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.util.Locale


class ChampionDetailsFragment : Fragment(R.layout.fragment_champion_detail), ChampionDetails.View {

    private var binding: FragmentChampionDetailBinding? = null
    private lateinit var presenter: ChampionDetails.Presenter
    private val args: ChampionDetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repositoryImpl =
            (activity?.application as Application).container.dataDragonRepository
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        activity?.actionBar?.setHomeAsUpIndicator(android.R.drawable.editbox_dropdown_dark_frame)

        presenter = ChampionDetailsPresenter(repositoryImpl, this)
        binding = FragmentChampionDetailBinding.bind(view)

        binding?.detailToolbarzada?.setupWithNavController(navController, appBarConfiguration)
        binding?.detailToolbarzada?.contentDescription = args.championName

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
        binding?.rvRelatedChampions?.apply {
            adapter = RotationAdapter(champions) { championId, championName, championVersion ->
                goToChampionDetailsScreen(championId, championName, championVersion)
            }
            layoutManager = GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
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
            val textShowError = TextView(requireContext())
            textShowError.text = getString(R.string.error_request)
            binding?.root?.addView(textShowError)
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

                    textDetailsChampionLore.apply {
                        var isLoreExpanded = false // Initialize a flag to track the state
                        text = lore
                        setOnClickListener {
                            if (isLoreExpanded) {
                                // If it's expanded, change to maxLines = 2 and ellipsize = end
                                maxLines = 2
                                ellipsize = TextUtils.TruncateAt.END
                                isLoreExpanded = false
                            } else {
                                // If it's not expanded, remove the maxLines limitation and ellipsize
                                maxLines = Int.MAX_VALUE
                                ellipsize = null
                                isLoreExpanded = true
                            }
                        }
                    }


                }
            }
        }
    }

    override fun showProgress(show: Boolean) {
        binding?.root?.apply {
            val progressBar = ProgressBar(requireContext())
            val constraintSet = ConstraintSet()
            addView(progressBar)
            progressBar.id = View.generateViewId()
            constraintSet.clone(this)
            constraintSet.centerHorizontally(progressBar.id, ConstraintSet.PARENT_ID)
            constraintSet.centerVertically(progressBar.id, ConstraintSet.PARENT_ID)
            // Apply the constraints
            constraintSet.applyTo(this)
            // Set ProgressBar's visibility
            progressBar.visibility = if (show) View.VISIBLE else View.GONE
        }
    }
}





