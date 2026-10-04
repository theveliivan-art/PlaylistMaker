package com.practicum.playlistmaker.ui.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MediaViewModel (private val track: Track,
                      private val playerInteractor: PlayerInteractor) : ViewModel() {

    private val mediaStateLD = MutableLiveData(MediaState(track = track))
    val state: LiveData<MediaState> = mediaStateLD

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private var progressJob: Job? = null

    init {
        preparePlayer()
    }

    private fun preparePlayer() {
        playerInteractor.prepare(
            url = track.previewUrl,
            onPrepared = {
                mediaStateLD.value = mediaStateLD.value?.copy(
                    playbackState = PlaybackState.PREPARED
                )
            },
            onCompletion = {
                stopProgressUpdates()
                mediaStateLD.value = mediaStateLD.value?.copy(
                    playbackState = PlaybackState.DEFAULT,
                    currentTime = "00:00"
                )
            }
        )
    }

    fun onPlayClicked() {
        val state = mediaStateLD.value?.playbackState
        if (state == PlaybackState.PREPARED || state == PlaybackState.PAUSED) startPlayer()
    }

    fun onPauseClicked() {
        if (mediaStateLD.value?.playbackState == PlaybackState.PLAYING) pausePlayer()
    }

    fun onScreenPaused() {
        pausePlayer()
    }

    private fun startPlayer() {
        playerInteractor.play()
        mediaStateLD.value = mediaStateLD.value?.copy(playbackState = PlaybackState.PLAYING)
        startProgressUpdates()
    }

    private fun pausePlayer() {
        if (mediaStateLD.value?.playbackState == PlaybackState.PLAYING) {
            playerInteractor.pause()
            mediaStateLD.value = mediaStateLD.value?.copy(playbackState = PlaybackState.PAUSED)
            stopProgressUpdates()
        }
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive) {
                val time = dateFormat.format(playerInteractor.currentPosition())
                mediaStateLD.value = mediaStateLD.value?.copy(currentTime = time)
                delay(DELAY_PLAYBACK_TIME)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopProgressUpdates()
        playerInteractor.release()
    }

    companion object {
        private const val DELAY_PLAYBACK_TIME = 400L
    }
}