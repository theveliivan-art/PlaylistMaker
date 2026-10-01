package com.practicum.playlistmaker.ui.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.domain.models.Track

class MediaViewModelFactory(private val track: Track) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MediaViewModel(track) as T
    }
}