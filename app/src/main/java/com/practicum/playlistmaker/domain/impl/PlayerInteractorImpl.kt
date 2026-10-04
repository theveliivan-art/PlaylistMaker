package com.practicum.playlistmaker.domain.impl

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.api.PlayerInteractor

class PlayerInteractorImpl: PlayerInteractor {

    private var mediaPlayer: MediaPlayer? = null
    private var currentUrl: String? = null

    override fun prepare(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    ) {
        release()
        currentUrl = url
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener { onPrepared() }
            setOnCompletionListener {
                onCompletion()
                reset()
                currentUrl?.let { setDataSource(it) }
                prepareAsync()
            }
            prepareAsync()
        }
    }

    override fun play() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun currentPosition(): Long =
        mediaPlayer?.currentPosition?.toLong() ?: 0L

}