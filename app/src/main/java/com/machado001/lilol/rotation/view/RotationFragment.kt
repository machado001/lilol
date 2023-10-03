package com.machado001.lilol.rotation.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.carousel.CarouselLayoutManager
import com.machado001.lilol.R
import com.machado001.lilol.databinding.FragmentRotationBinding
import com.machado001.lilol.rotation.Rotation
import com.machado001.lilol.rotation.model.Champion
import com.machado001.lilol.rotation.model.RotationFakeRemoteDataSource
import com.machado001.lilol.rotation.model.RotationRepository
import com.machado001.lilol.rotation.presentation.RotationPresenter
import kotlinx.coroutines.launch


class RotationFragment : Fragment(R.layout.fragment_rotation), Rotation.View {

    private lateinit var presenter: Rotation.Presenter
    private var binding: FragmentRotationBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = RotationPresenter(RotationRepository(RotationFakeRemoteDataSource()), this)
        binding = FragmentRotationBinding.bind(view)

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
        date: String
    ) {
        binding?.let {
            with(it) {

                rvRotationMain.apply {
                    adapter = RotationAdapter(freeChampionIds.toMutableList())
                    layoutManager = CarouselLayoutManager()
                }

                rvRotationNewPlayers.apply {
                    adapter = RotationAdapter(freeChampionIdsForNewPlayers.toMutableList())
                    layoutManager = CarouselLayoutManager()
                }

                txtRotationTitle.apply {
                    visibility = View.VISIBLE
                    text = getString(R.string.week_rotation, date)
                }

                txtRotationTitleTwo.apply {
                    visibility = View.VISIBLE
                    text = getString(
                        R.string.week_rotation_two,
                        level.toString()
                    )
                }
            }
        }
    }
}