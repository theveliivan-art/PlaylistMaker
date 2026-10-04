package com.practicum.playlistmaker.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.api.ThemeInteractor

class SettingsViewModel (private val themeInteractor: ThemeInteractor) : ViewModel() {

    private val isDarkThemeLD = MutableLiveData(themeInteractor.isDarkTheme())
    val isDarkTheme: LiveData<Boolean> = isDarkThemeLD

    fun switchTheme(enabled: Boolean) {
        themeInteractor.setDarkTheme(enabled)
        isDarkThemeLD.value = enabled
    }
}
