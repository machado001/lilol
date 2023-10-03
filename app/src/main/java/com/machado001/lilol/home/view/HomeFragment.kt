package com.machado001.lilol.home.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.machado001.lilol.R
import com.machado001.lilol.databinding.FragmentHomeBinding


class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}