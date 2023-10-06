package com.machado001.lilol.rotation.view


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.carousel.CarouselLayoutManager
import com.machado001.lilol.Application
import com.machado001.lilol.R
import com.machado001.lilol.common.model.data.Champion
import com.machado001.lilol.databinding.FragmentRotationBinding
import com.machado001.lilol.rotation.Rotation
import com.machado001.lilol.rotation.presentation.RotationPresenter
import kotlinx.coroutines.launch


class RotationFragment : Fragment(R.layout.fragment_rotation), Rotation.View {

    private lateinit var presenter: Rotation.Presenter
    private var binding: FragmentRotationBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val championsManager = (activity?.application as Application)
            .container.championsManager

        binding = FragmentRotationBinding.bind(view)
        presenter = RotationPresenter(championsManager, this)

        viewLifecycleOwner.lifecycleScope.launch {
            presenter.fetchRotations()
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

    override fun showSuccess(
        freeChampionIds: List<Champion>,
        freeChampionIdsForNewPlayers: List<Champion>,
        level: Int,
    ) {
        binding?.let {
            with(it) {

                rvRotationMain.apply {
                    adapter = RotationAdapter(freeChampionIds.toList())
                    layoutManager = CarouselLayoutManager()
                }
                rvRotationNewPlayers.apply {
                    adapter = RotationAdapter(freeChampionIdsForNewPlayers.toList())
                    layoutManager = CarouselLayoutManager()
                }

                txtRotationTitle.apply {
                    visibility = View.VISIBLE
                }
                newPlayerRange.apply {
                    visibility = View.VISIBLE
                    text =
                        getString(
                            R.string.new_player_range,
                            level,
                            freeChampionIdsForNewPlayers.size
                        )
                }

                normalPlayerRange.apply {
                    visibility = View.VISIBLE
                    text =
                        getString(
                            R.string.normal_player_range,
                            level,
                            freeChampionIds.size
                        )
                }
            }
        }
    }
}