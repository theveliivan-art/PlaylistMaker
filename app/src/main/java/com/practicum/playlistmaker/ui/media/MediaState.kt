package com.practicum.playlistmaker.ui.media

import com.practicum.playlistmaker.domain.models.Track

data class MediaState(
    val track: Track,
    val currentTime: String = "00:00",
    val playbackState: PlaybackState = PlaybackState.DEFAULT
) {
    val isPlaying: Boolean get() = playbackState == PlaybackState.PLAYING
    val isPlayEnabled: Boolean
        get() = playbackState == PlaybackState.PREPARED || playbackState == PlaybackState.PAUSED
}
