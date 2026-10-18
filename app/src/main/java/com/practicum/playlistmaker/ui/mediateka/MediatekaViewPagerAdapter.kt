package com.practicum.playlistmaker.ui.mediateka

import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.fragment.app.Fragment

class MediatekaViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

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