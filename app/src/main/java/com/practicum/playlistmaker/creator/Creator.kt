package com.practicum.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.ITunesSearchAPI
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.SearchTracksInteractor
import com.practicum.playlistmaker.domain.api.ThemeInteractor
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.SearchTracksInteractorImpl
import com.practicum.playlistmaker.domain.impl.ThemeInteractorImpl
import com.practicum.playlistmaker.domain.impl.PlayerInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {

    private const val PLAYLISTMAKER_PREFERENCES = "playlistmaker_preferences"
    private const val ITUNES_URL = "https://itunes.apple.com"

    private val gson: Gson = Gson()
    private lateinit var application: Application

    fun initialize(app: Application) {
        application = app
    }
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ITUNES_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val api: ITunesSearchAPI by lazy {
        retrofit.create(ITunesSearchAPI::class.java)
    }

    private val prefs: SharedPreferences by lazy {
        application.getSharedPreferences(PLAYLISTMAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

    fun provideSearchTracksInteractor(): SearchTracksInteractor =
        SearchTracksInteractorImpl(TracksRepositoryImpl(api))

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor =
        SearchHistoryInteractorImpl(SearchHistoryRepositoryImpl(prefs, gson))

    fun provideThemeInteractor(): ThemeInteractor =
        ThemeInteractorImpl(SettingsRepositoryImpl(prefs))

    fun providePlayerInteractor(): PlayerInteractor = PlayerInteractorImpl()
}