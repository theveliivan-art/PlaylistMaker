package com.practicum.playlistmaker.ui.main

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var isImeVisible = false
    private var currentDestinationId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            isImeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            updateBottomNavVisibility()
            insets
        }

        val navItemColors = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf()
            ),
            intArrayOf(
                ContextCompat.getColor(this, R.color.MainTheme),
                ContextCompat.getColor(this, R.color.BlackWhite)
            )
        )
        binding.bottomNavigationView.itemIconTintList = navItemColors
        binding.bottomNavigationView.itemTextColor = navItemColors

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.root_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentDestinationId = destination.id
            updateBottomNavVisibility()
        }
    }

    private fun updateBottomNavVisibility() {
        binding.bottomNavigationView.isVisible =
            !isImeVisible && currentDestinationId != R.id.mediaFragment
    }
}