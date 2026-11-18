package com.practicum.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked -> viewModel.switchTheme(checked) }

        binding.settingsShareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SENDTO).apply {
                putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_share_url))
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.settings_share_choose_title)))
        }

        binding.settingsSupportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.settings_support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_support_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_support_text))
            }
            startActivity(supportIntent)
        }

        binding.settingsArrowForwardButton.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.settings_arrow_forward_url))
                )
            )
        }

        viewModel.isDarkTheme.observe(this) { isDark ->
            binding.themeSwitcher.isChecked = isDark
            val newMode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
            if (AppCompatDelegate.getDefaultNightMode() != newMode) {
                AppCompatDelegate.setDefaultNightMode(newMode)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}