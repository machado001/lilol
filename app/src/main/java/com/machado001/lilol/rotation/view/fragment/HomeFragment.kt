package com.machado001.lilol.rotation.view.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.machado001.lilol.Application
import com.machado001.lilol.R
import com.machado001.lilol.databinding.FragmentHomeBinding
import com.machado001.lilol.rotation.Home
import com.machado001.lilol.rotation.presentation.HomePresenter
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home), Home.View {
    override lateinit var presenter: Home.Presenter

    private var binding: FragmentHomeBinding? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        val repositoryImpl = (activity?.application as Application).container.dataDragonRepository
        presenter = HomePresenter(repositoryImpl, this)
        setupOnClickListeners()
        viewLifecycleOwner.lifecycleScope.launch {
            presenter.getActualGameVersion()
        }
    }

    override fun showGameVersion(version: String) {
        binding?.homeTextPatch?.text = getString(R.string.patch, version)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        presenter.onDestroy()
    }

    override fun goToAllChampionsScreen() {
        val action = HomeFragmentDirections.actionHomeFragmentToAllChampionsFragment()
        findNavController().navigate(action)
    }

    override fun goToFreeWeekScreen() {
        val action = HomeFragmentDirections.actionHomeFragmentToRotationFragment()
        findNavController().navigate(action)
    }

    override fun setupOnClickListeners() {
        binding?.apply {

            homeLinearFreeWeekWrapper.apply {
                setOnClickListener {
                    presenter.onNavigateToFreeWeekScreen()
                }
            }

            homeLinearAllChampionsWrapper.apply {
                setOnClickListener {
                    presenter.onNavigateToAllChampionsScreen()
                }
            }
        }


    }
}