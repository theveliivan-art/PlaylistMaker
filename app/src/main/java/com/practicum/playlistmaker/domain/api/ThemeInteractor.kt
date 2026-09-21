package com.practicum.playlistmaker.domain.api

interface ThemeInteractor {
    fun isDarkTheme(): Boolean
    fun setDarkTheme(enabled: Boolean)
}