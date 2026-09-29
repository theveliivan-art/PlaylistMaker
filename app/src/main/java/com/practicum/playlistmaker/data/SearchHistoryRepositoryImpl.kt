package com.practicum.playlistmaker.data

import android.content.SharedPreferences
import com.practicum.playlistmaker.data.dto.TrackDTO
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(private val sharedPrefs: SharedPreferences,
                                  private val gson: Gson):SearchHistoryRepository {

    override fun getHistory(): List<Track> {
        val json = sharedPrefs.getString(HISTORY_SEARCH_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<TrackDTO>>() {}.type
        val dtos: List<TrackDTO> = gson.fromJson(json, type) ?: return emptyList()
        return dtos.map(TrackMapper::toDomain)
    }

    override fun saveHistory(tracks: List<Track>) {
        val dtos = tracks.map(TrackMapper::toDto)
        sharedPrefs.edit().putString(HISTORY_SEARCH_KEY, gson.toJson(dtos)).apply()
    }

    private companion object {
        const val HISTORY_SEARCH_KEY = "history_search"
    }
}
