package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.data.network.ITunesSearchAPI
import com.practicum.playlistmaker.domain.api.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TracksRepositoryImpl(private val api: ITunesSearchAPI):TracksRepository {

    override fun searchTracks(term: String): Flow<List<Track>> = flow {
        val response = api.search(term)
        emit(response.results.map(TrackMapper::toDomain))
    }.flowOn(Dispatchers.IO)
}