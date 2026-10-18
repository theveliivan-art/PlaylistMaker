package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.SearchTracksInteractor
import com.practicum.playlistmaker.domain.api.ThemeInteractor
import com.practicum.playlistmaker.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.SearchTracksInteractorImpl
import com.practicum.playlistmaker.domain.impl.ThemeInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<SearchHistoryInteractor> { SearchHistoryInteractorImpl(get()) }
    single<SearchTracksInteractor> { SearchTracksInteractorImpl(get()) }
    single<ThemeInteractor> { ThemeInteractorImpl(get()) }
    single<PlayerInteractor> { PlayerInteractorImpl() }

}