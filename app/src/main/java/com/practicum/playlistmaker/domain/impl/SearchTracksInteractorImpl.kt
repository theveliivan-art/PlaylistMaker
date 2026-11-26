package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.api.SearchTracksInteractor
import kotlinx.coroutines.flow.Flow

class SearchTracksInteractorImpl(private val repository: TracksRepository):SearchTracksInteractor {
    override fun searchTracks(term: String): Flow<List<Track>> = repository.searchTracks(term)
}