package com.machado001.lilol.rotation.view.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy
import com.machado001.lilol.R
import com.machado001.lilol.common.model.data.Skin
import com.machado001.lilol.databinding.BottomSheetSkinsBinding
import com.machado001.lilol.rotation.view.adapter.SkinsAdapter

class SkinsBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSkinsBinding? = null
    private val binding get() = _binding!!

    private lateinit var skins: List<Skin>
    private lateinit var championId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            skins = it.getParcelableArrayList(ARG_SKINS) ?: emptyList()
            championId = it.getString(ARG_CHAMPION_ID) ?: ""
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog is BottomSheetDialog) {
            dialog.behavior.isDraggable = true
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSkinsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtSheetTitle.text =
            "${getString(R.string.available_skins_label)} (${skins.size})"

        binding.rvSkinsCarousel.apply {
            layoutManager = CarouselLayoutManager(HeroCarouselStrategy())
            val skinsAdapter = SkinsAdapter(skins, championId)
            adapter = skinsAdapter
            val snapHelper = CarouselSnapHelper()
            snapHelper.attachToRecyclerView(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SkinsBottomSheetFragment"
        private const val ARG_SKINS = "skins"
        private const val ARG_CHAMPION_ID = "champion_id"

        fun newInstance(skins: List<Skin>, championId: String): SkinsBottomSheetFragment {
            val fragment = SkinsBottomSheetFragment()
            val args = Bundle().apply {
                putParcelableArrayList(ARG_SKINS, ArrayList(skins))
                putString(ARG_CHAMPION_ID, championId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
