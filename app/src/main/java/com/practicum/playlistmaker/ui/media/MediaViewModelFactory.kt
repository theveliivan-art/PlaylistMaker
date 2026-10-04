package com.practicum.playlistmaker.ui.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.api.PlayerInteractor

class MediaViewModelFactory(
    private val track: Track,
    private val playerInteractor: PlayerInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MediaViewModel(track, playerInteractor) as T
    }
}