package com.practicum.playlistmaker.data

import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val sharedPrefs: SharedPreferences):SettingsRepository {

    override fun isDarkTheme(): Boolean =
        sharedPrefs.getBoolean(DARK_THEME_KEY, false)

    override fun setDarkTheme(enabled: Boolean) {
        sharedPrefs.edit().putBoolean(DARK_THEME_KEY, enabled).apply()
    }

    private companion object {
        const val DARK_THEME_KEY = "dark_theme"
    }
}