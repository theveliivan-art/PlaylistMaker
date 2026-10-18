package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.media.MediaViewModel
import com.practicum.playlistmaker.ui.search.SearchViewModel
import com.practicum.playlistmaker.ui.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { (track: Track) ->MediaViewModel(track, get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }

}