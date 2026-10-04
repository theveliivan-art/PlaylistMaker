package com.practicum.playlistmaker.domain.api

interface PlayerInteractor {
    fun prepare(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    )

    fun play()
    fun pause()
    fun release()

    fun currentPosition(): Long
}