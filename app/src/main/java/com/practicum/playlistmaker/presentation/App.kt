package com.practicum.playlistmaker.presentation
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Creator.initialize(this)
        applyTheme(Creator.provideThemeInteractor().isDarkTheme())
    }

    fun isDarkTheme(): Boolean = Creator.provideThemeInteractor().isDarkTheme()

    fun switchTheme(enabled: Boolean) {
        Creator.provideThemeInteractor().setDarkTheme(enabled)
        applyTheme(enabled)
    }

    private fun applyTheme(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}

