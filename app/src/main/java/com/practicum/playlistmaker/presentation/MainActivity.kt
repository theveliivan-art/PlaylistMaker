package com.practicum.playlistmaker.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.main_search_button).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        findViewById<Button>(R.id.main_media_button).setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }
        findViewById<Button>(R.id.main_settings_button).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}