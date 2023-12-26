package com.machado001.lilol.rotation.view.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.machado001.lilol.Application
import com.machado001.lilol.R
import com.machado001.lilol.common.model.data.Champion
import com.machado001.lilol.databinding.FragmentAllChampionsBinding
import com.machado001.lilol.rotation.AllChampions
import com.machado001.lilol.rotation.presentation.AllChampionsPresenter
import com.machado001.lilol.rotation.view.adapter.AllChampionsAdapter
import kotlinx.coroutines.launch

class AllChampionsFragment : Fragment(R.layout.fragment_all_champions), AllChampions.View {
    
    private var binding: FragmentAllChampionsBinding? = null
    override lateinit var presenter: AllChampions.Presenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding = FragmentAllChampionsBinding.bind(view)
        val repositoryImpl = (activity?.application as Application).container.dataDragonRepository
        presenter = AllChampionsPresenter(this, repositoryImpl)
        binding?.allChampionsToolbar?.setupWithNavController(navController, appBarConfiguration)

        viewLifecycleOwner.lifecycleScope.launch {

            presenter.apply {
                getAllChampions()
                getAllRoles().map { championRole ->
                    populateChips(championRole)
                }
            }
        }
    }

    override fun populateChips(championRole: String) {
        binding?.chipGroup?.apply {

            val chipStyle = ChipDrawable.createFromAttributes(
                requireContext(),
                null,
                0,
                com.google.android.material.R.style.Widget_Material3_Chip_Filter
            )

            val chip = Chip(requireContext()).apply {
                setChipDrawable(chipStyle)
                text = championRole
                isChecked
                id = View.generateViewId()

            }
            this.addView(chip)

            setOnCheckedStateChangeListener { _, ints ->
                val selectedChips = ints.map {
                    findViewById<Chip>(it).text
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    (presenter as AllChampionsPresenter).filterChampions(selectedChips)
                }
            }
        }
    }

    override fun showErrorMessage(msg: String) {
        //todo
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDestroy()
        binding = null
    }

    override fun showProgress(enabled: Boolean) {
        val enabledLogic = if (enabled) View.GONE else View.VISIBLE
        binding?.apply {
            rvAllChampions.visibility = enabledLogic
            allChampionsToolbar.visibility = enabledLogic
            chipGroup.visibility = enabledLogic
            allChampionsProgress.visibility = if (enabled) View.VISIBLE else View.GONE
        }
    }

    override fun showChampions(allChampions: List<Champion>) {
        binding?.apply {

            rvAllChampions.apply {
                adapter =
                    AllChampionsAdapter(allChampions) { championKey, championName, championVersion ->
                        goToChampionDetails(championKey, championName, championVersion)
                    }
                layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            }

        }
    }

    override fun goToChampionDetails(
        championId: String,
        championName: String,
        championVersion: String,
    ) {
        val action =
            AllChampionsFragmentDirections
                .actionAllChampionsFragmentToChampionDetailFragment(
                    championId,
                    championName,
                    championVersion
                )
        findNavController().navigate(action)
    }

}