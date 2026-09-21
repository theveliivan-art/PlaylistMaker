package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.api.SearchTracksInteractor

class SearchTracksInteractorImpl(private val repository: TracksRepository):SearchTracksInteractor {
    override fun searchTracks(
        term: String,
        onSuccess: (List<Track>) -> Unit,
        onError: (Throwable) -> Unit
        ) = repository.searchTracks(term, onSuccess, onError)
}