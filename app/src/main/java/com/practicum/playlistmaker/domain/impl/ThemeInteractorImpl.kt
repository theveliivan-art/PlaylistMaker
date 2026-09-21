package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SettingsRepository
import com.practicum.playlistmaker.domain.api.ThemeInteractor

class ThemeInteractorImpl(private val repository: SettingsRepository):ThemeInteractor {

    override fun isDarkTheme(): Boolean = repository.isDarkTheme()

    override fun setDarkTheme(enabled: Boolean) = repository.setDarkTheme(enabled)

}