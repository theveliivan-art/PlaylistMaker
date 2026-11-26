package com.practicum.playlistmaker.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.SearchTracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchTracksInteractor: SearchTracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor) : ViewModel() {

    private val searchStateLD = MutableLiveData(
        SearchState(
            history = searchHistoryInteractor.getHistory(),
            showHistory = searchHistoryInteractor.getHistory().isNotEmpty()
        )
    )
    val state: LiveData<SearchState> = searchStateLD
    private var searchJob: Job? = null
    fun onSearchTextChanged(text: String) {
        if (text == searchStateLD.value?.searchText) return

        searchJob?.cancel()

        if (text.isEmpty()) {
            val history = searchHistoryInteractor.getHistory()
            searchStateLD.value = searchStateLD.value?.copy(
                searchText = text,
                tracks = emptyList(),
                isLoading = false,
                showEmpty = false,
                showError = false,
                showHistory = history.isNotEmpty()
            )
            return
        }

        searchStateLD.value = searchStateLD.value?.copy(
            searchText = text,
            showHistory = false,
            showEmpty = false,
            showError = false
        )

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            performSearch(text)
        }
    }

    fun onClearClicked() {
        searchJob?.cancel()
        val history = searchHistoryInteractor.getHistory()
        searchStateLD.value = searchStateLD.value?.copy(
            searchText = "",
            tracks = emptyList(),
            isLoading = false,
            showEmpty = false,
            showError = false,
            showHistory = history.isNotEmpty()
        )
    }

    fun onClearHistoryClicked() {
        searchHistoryInteractor.clearHistory()
        searchStateLD.value = searchStateLD.value?.copy(
            history = emptyList(),
            showHistory = false
        )
    }

    fun onRetryClicked() {
        val term = searchStateLD.value?.searchText.orEmpty()
        if (term.isEmpty()) return
        searchJob?.cancel()
        searchJob = viewModelScope.launch { performSearch(term) }
    }

    fun onTrackClicked(track: Track) {
        searchHistoryInteractor.addTrack(track)
        searchStateLD.value = searchStateLD.value?.copy(history = searchHistoryInteractor.getHistory())
    }

    private suspend fun performSearch(term: String) {
        searchStateLD.value = searchStateLD.value?.copy(isLoading = true, showEmpty = false, showError = false)
        searchTracksInteractor.searchTracks(term).catch {
                searchStateLD.value = searchStateLD.value?.copy(
                    isLoading = false,
                    showError = true,
                    showEmpty = false
                )
            }
            .collect { result ->
                searchStateLD.value = searchStateLD.value?.copy(
                    tracks = result,
                    isLoading = false,
                    showEmpty = result.isEmpty(),
                    showError = false
                )
            }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
