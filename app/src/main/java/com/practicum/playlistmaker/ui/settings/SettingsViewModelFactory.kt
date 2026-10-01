package com.practicum.playlistmaker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.domain.api.ThemeInteractor

class SettingsViewModelFactory(private val themeInteractor: ThemeInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(themeInteractor) as T
    }
}