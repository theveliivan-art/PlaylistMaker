package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository):SearchHistoryInteractor {

    override fun getHistory(): List<Track> = repository.getHistory()

    override fun addTrack(track: Track) {
        val history = repository.getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        while (history.size > MAX_HISTORY_SIZE) history.removeAt(history.size - 1)
        repository.saveHistory(history)
    }

    override fun clearHistory() = repository.saveHistory(emptyList())

    private companion object {
        const val MAX_HISTORY_SIZE = 10
    }
}