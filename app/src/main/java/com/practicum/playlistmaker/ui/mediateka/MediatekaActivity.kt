package com.practicum.playlistmaker.ui.mediateka

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.R
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.databinding.ActivityMediatekaBinding

class MediatekaActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMediatekaBinding

    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMediatekaBinding .inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.mediatekaButtonBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.viewPager.adapter = MediatekaViewPagerAdapter(supportFragmentManager, lifecycle)

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.mediateka_tab_favorites)
                else -> tab.text = getString(R.string.mediateka_tab_playlists)
            }
        }
        tabMediator.attach()

    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}