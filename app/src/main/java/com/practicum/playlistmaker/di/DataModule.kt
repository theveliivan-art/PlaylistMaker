package com.practicum.playlistmaker.di

import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.Gson
import com.practicum.playlistmaker.data.network.ITunesSearchAPI
import org.koin.android.ext.koin.androidContext
import android.content.Context

private const val ITUNES_URL = "https://itunes.apple.com"
private const val PLAYLISTMAKER_PREFERENCES = "playlistmaker_preferences"

val dataModule = module {

    single {
        androidContext()
            .getSharedPreferences(PLAYLISTMAKER_PREFERENCES, Context.MODE_PRIVATE) }

    single<Gson> { Gson() }

    single<ITunesSearchAPI> {
        Retrofit.Builder()
            .baseUrl(ITUNES_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesSearchAPI::class.java)
    }

}