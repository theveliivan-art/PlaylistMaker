package com.practicum.playlistmaker.ui.search

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.SearchTracksInteractor
import com.practicum.playlistmaker.domain.models.Track

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

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    fun onSearchTextChanged(text: String) {
        if (text == searchStateLD.value?.searchText) return

        searchRunnable?.let { handler.removeCallbacks(it) }

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

        val runnable = Runnable { performSearch(text) }
        searchRunnable = runnable
        handler.postDelayed(runnable, SEARCH_DEBOUNCE_DELAY)
    }

    fun onClearClicked() {
        searchRunnable?.let { handler.removeCallbacks(it) }
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
        if (term.isNotEmpty()) performSearch(term)
    }

    fun onTrackClicked(track: Track) {
        searchHistoryInteractor.addTrack(track)
        searchStateLD.value = searchStateLD.value?.copy(history = searchHistoryInteractor.getHistory())
    }

    private fun performSearch(term: String) {
        searchStateLD.value = searchStateLD.value?.copy(isLoading = true, showEmpty = false, showError = false)
        searchTracksInteractor.searchTracks(
            term,
            onSuccess = { result ->
                searchStateLD.value = searchStateLD.value?.copy(
                    tracks = result,
                    isLoading = false,
                    showEmpty = result.isEmpty(),
                    showError = false
                )
            },
            onError = {
                searchStateLD.value = searchStateLD.value?.copy(
                    isLoading = false,
                    showError = true,
                    showEmpty = false
                )
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
