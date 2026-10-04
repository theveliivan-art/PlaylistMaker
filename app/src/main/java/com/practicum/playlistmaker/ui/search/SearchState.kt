package com.practicum.playlistmaker.ui.search

import com.practicum.playlistmaker.domain.models.Track

data class SearchState(
    val searchText: String = "",
    val tracks: List<Track> = emptyList(),
    val history: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val showEmpty: Boolean = false,
    val showError: Boolean = false,
    val showHistory: Boolean = false
)
