package com.practicum.playlistmaker
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import android.content.SharedPreferences

const val PLAYLISTMAKER_PREFERENCES = "playlistmaker_preferences"
const val DARK_THEME_KEY = "dark_theme"
const val HISTORY_SEARCH_KEY = "history_search"
class App : Application() {

    var darkTheme = false
        private set
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(DARK_THEME_KEY, false)
        applyTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        applyTheme(darkThemeEnabled)
        sharedPrefs.edit().putBoolean(DARK_THEME_KEY, darkThemeEnabled).apply()

    }
    private fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
    fun isDarkTheme(): Boolean = sharedPrefs.getBoolean(DARK_THEME_KEY, false)
}

