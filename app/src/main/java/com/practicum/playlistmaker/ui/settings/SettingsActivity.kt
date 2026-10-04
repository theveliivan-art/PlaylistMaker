package com.practicum.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.R
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.creator.Creator

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(Creator.provideThemeInteractor())
        )[SettingsViewModel::class.java]

        findViewById<ImageView>(R.id.settings_back_button).setOnClickListener { finish() }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        themeSwitcher.setOnCheckedChangeListener { _, checked -> viewModel.switchTheme(checked) }

        findViewById<ImageView>(R.id.settings_share_button).setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SENDTO).apply {
                putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_share_url))
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.settings_share_choose_title)))
        }

        findViewById<ImageView>(R.id.settings_support_button).setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.settings_support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_support_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_support_text))
            }
            startActivity(supportIntent)
        }

        findViewById<ImageView>(R.id.settings_arrow_forward_button).setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.settings_arrow_forward_url))
                )
            )
        }

        viewModel.isDarkTheme.observe(this) { isDark ->
            themeSwitcher.isChecked = isDark
            val newMode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
            if (AppCompatDelegate.getDefaultNightMode() != newMode) {
                AppCompatDelegate.setDefaultNightMode(newMode)
            }
        }
    }
}