package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface SearchTracksInteractor {
    fun searchTracks(
        term: String,
        onSuccess: (List<Track>) -> Unit,
        onError: (Throwable) -> Unit
    )
}