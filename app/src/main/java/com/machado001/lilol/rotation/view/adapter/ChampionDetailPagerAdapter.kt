package com.machado001.lilol.rotation.view.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.machado001.lilol.R

private const val PAGER_COUNT = 4
private const val ARG_OBJECT = "object"


class ChampionDetailPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = PAGER_COUNT
    override fun createFragment(position: Int): Fragment {
        val fragment = Frag()
        fragment.arguments = Bundle().apply {
            putInt(ARG_OBJECT, position + 1)
        }
        return fragment
    }
}


class Frag() : Fragment(R.layout.fragment_champion_detail_remake_tab)
