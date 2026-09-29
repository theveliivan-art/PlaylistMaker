package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun getHistory(): List<Track>
    fun saveHistory(tracks: List<Track>)
}