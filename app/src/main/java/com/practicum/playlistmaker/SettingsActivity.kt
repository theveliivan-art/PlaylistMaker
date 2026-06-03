package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button_back_click = findViewById<ImageView>(R.id.settings_back_button)
        button_back_click.setOnClickListener {
            finish()
        }
        val button_share_click = findViewById<ImageView>(R.id.settings_share_button)
        button_share_click.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_share_url))
            startActivity(Intent.createChooser(shareIntent, getString(R.string.settings_share_choose_title)))
        }
        val button_support_click = findViewById<ImageView>(R.id.settings_support_button)
        button_support_click.setOnClickListener {
           val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.settings_support_email)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_support_subject))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_support_text))
            startActivity(supportIntent)
        }
        val button_arrow_forward_click = findViewById<ImageView>(R.id.settings_arrow_forward_button)
        button_arrow_forward_click.setOnClickListener {
            val url = getString(R.string.settings_arrow_forward_url)
            val arrowForwardIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(arrowForwardIntent)
        }

    }
}