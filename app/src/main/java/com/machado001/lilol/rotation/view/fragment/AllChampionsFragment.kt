package com.machado001.lilol.rotation.view.fragment

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.machado001.lilol.Application
import com.machado001.lilol.R
import com.machado001.lilol.common.model.data.Champion
import com.machado001.lilol.common.view.fadeOutAndGone
import com.machado001.lilol.common.view.startSkeletonPulse
import com.machado001.lilol.common.view.stopSkeletonPulse
import com.machado001.lilol.databinding.FragmentAllChampionsBinding
import com.machado001.lilol.rotation.AllChampions
import com.machado001.lilol.rotation.presentation.AllChampionsPresenter
import com.machado001.lilol.rotation.view.adapter.AllChampionsAdapter
import kotlinx.coroutines.launch

class AllChampionsFragment : Fragment(R.layout.fragment_all_champions), AllChampions.View {

    private var binding: FragmentAllChampionsBinding? = null
    override lateinit var presenter: AllChampions.Presenter
    private var championsAdapter: AllChampionsAdapter? = null
    private var bottomNavHeight: Float = 0f
    private var paginationHidden: Boolean = false
    private var hasError: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding = FragmentAllChampionsBinding.bind(view).also {
            it.allChampionsToolbar.setupWithNavController(navController, appBarConfiguration)
        }

        val repositoryImpl = (requireActivity().application as Application).container.dataDragonRepository
        presenter = AllChampionsPresenter(this, repositoryImpl)

        setupRecycler()
        setupSearch()
        setupListeners()
        setupBottomNavigationInteraction()

        viewLifecycleOwner.lifecycleScope.launch {
            presenter.loadChampions()
        }
    }

    private fun setupRecycler() {
        championsAdapter = AllChampionsAdapter(emptyList()) { championKey, championName, championVersion ->
            goToChampionDetails(championKey, championName, championVersion)
        }
        binding?.rvAllChampions?.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = championsAdapter
            setHasFixedSize(true)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        hidePagination()
                    } else if (dy < 0) {
                        showPagination()
                    }
                }
            })
        }
    }

    private fun setupSearch() {
        binding?.allChampionsSearchEditText?.doAfterTextChanged {
            presenter.onSearchQueryChanged(it?.toString().orEmpty())
        }
    }

    private fun setupBottomNavigationInteraction() {
        val navView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        navView.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
            bottomNavHeight = v.height.toFloat()
            if (!paginationHidden) {
                elevatePaginationAboveNav()
            }
        }
        navView.post {
            bottomNavHeight = navView.height.toFloat()
            elevatePaginationAboveNav()
        }
    }

    private fun elevatePaginationAboveNav() {
        binding?.allChampionsPaginationContainer?.translationY = -bottomNavHeight
    }

    private fun showPagination() {
        if (!paginationHidden) return
        paginationHidden = false
        binding?.allChampionsPaginationContainer
            ?.animate()
            ?.translationY(-bottomNavHeight)
            ?.setDuration(150)
            ?.start()
    }

    private fun hidePagination() {
        if (paginationHidden) return
        paginationHidden = true
        binding?.allChampionsPaginationContainer
            ?.animate()
            ?.translationY(0f)
            ?.setDuration(150)
            ?.start()
    }

    private fun setupListeners() {
        binding?.apply {
            allChampionsButtonNext.setOnClickListener { presenter.onNextPage() }
            allChampionsButtonPrevious.setOnClickListener { presenter.onPreviousPage() }
        }
    }

    override fun showChampions(allChampions: List<Champion>) {
        hasError = false
        championsAdapter?.updateData(allChampions)
        binding?.rvAllChampions?.visibility = if (allChampions.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun showRoles(roles: Set<String>) {
        binding?.chipGroup?.apply {
            setOnCheckedStateChangeListener(null)
            removeAllViews()
            roles.sorted().forEach { role ->
                val chipDrawable = ChipDrawable.createFromAttributes(
                    requireContext(),
                    null,
                    0,
                    com.google.android.material.R.style.Widget_Material3_Chip_Filter
                )
                val chip = Chip(requireContext()).apply {
                    setChipDrawable(chipDrawable)
                    text = role
                    isCheckable = true
                    id = View.generateViewId()
                }
                addView(chip)
            }
            setOnCheckedStateChangeListener { group, checkedIds ->
                val selectedRoles = checkedIds.map { id ->
                    group.findViewById<Chip>(id).text
                }
                presenter.onRolesChanged(selectedRoles)
            }
        }
    }

    override fun showPagination(currentPage: Int, totalPages: Int) {
        binding?.apply {
            allChampionsPaginationLabel.text = if (totalPages > 0) {
                getString(R.string.pagination_label, currentPage, totalPages)
            } else {
                getString(R.string.pagination_label, 0, 0)
            }
            allChampionsButtonPrevious.isEnabled = currentPage > 1
            allChampionsButtonNext.isEnabled = currentPage < totalPages
            allChampionsPaginationContainer.visibility =
                if (totalPages > 1) View.VISIBLE else View.GONE
        }
    }

    override fun showEmptyState(show: Boolean, query: String) {
        binding?.apply {
            allChampionsEmptyState.visibility = if (show) View.VISIBLE else View.GONE
            if (show && query.isNotBlank()) {
                allChampionsEmptyState.text =
                    getString(R.string.champions_empty_state_with_query, query)
            } else if (show) {
                allChampionsEmptyState.text = getString(R.string.champions_empty_state)
            }
        }
    }

    override fun showProgress(enabled: Boolean) {
        binding?.apply {
            if (enabled) hasError = false
            if (enabled) {
                allChampionsLoadingPlaceholder.animate().cancel()
                allChampionsLoadingPlaceholder.alpha = 1f
                allChampionsLoadingPlaceholder.visibility = View.VISIBLE
                allChampionsLoadingPlaceholder.startSkeletonPulse()
            } else {
                allChampionsLoadingPlaceholder.stopSkeletonPulse()
                allChampionsLoadingPlaceholder.fadeOutAndGone()
            }
            if (hasError && !enabled) {
                // Keep error state; do not reshow content
                return
            }
            val contentVisibility = if (enabled) View.GONE else View.VISIBLE
            rvAllChampions.visibility = contentVisibility
            allChampionsPaginationContainer.visibility = contentVisibility
            allChampionsEmptyState.visibility =
                if (enabled) View.GONE else allChampionsEmptyState.visibility
            allChampionsSearchLayout.visibility = contentVisibility
            chipGroup.visibility = contentVisibility
        }
    }

    override fun showErrorMessage(msg: String) {
        hasError = true
        binding?.apply {
            allChampionsLoadingPlaceholder.stopSkeletonPulse()
            allChampionsLoadingPlaceholder.visibility = View.GONE
            rvAllChampions.visibility = View.GONE
            allChampionsPaginationContainer.visibility = View.GONE
            allChampionsEmptyState.visibility = View.VISIBLE
            allChampionsEmptyState.text = msg.ifBlank {
                getString(R.string.champions_error_message)
            }
            allChampionsEmptyState.textAlignment = View.TEXT_ALIGNMENT_CENTER
            allChampionsSearchLayout.visibility = View.VISIBLE
            chipGroup.visibility = View.VISIBLE
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

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDestroy()
        binding = null
    }

    override fun onResume() {
        super.onResume()
        // Ensure pagination is correctly positioned when returning to this screen
        elevatePaginationAboveNav()
    }
}
