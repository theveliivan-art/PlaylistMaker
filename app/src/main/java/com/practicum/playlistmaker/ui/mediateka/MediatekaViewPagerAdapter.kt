package com.practicum.playlistmaker.ui.mediateka

import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.fragment.app.Fragment

class MediatekaViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FavoriteFragment.newInstance()
            else -> PlaylistFragment.newInstance()
        }
    }
}