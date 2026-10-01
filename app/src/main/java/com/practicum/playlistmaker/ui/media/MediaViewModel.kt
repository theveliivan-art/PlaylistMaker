package com.practicum.playlistmaker.ui.media

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class MediaViewModel (private val track: Track) : ViewModel() {

    private val mediaStateLD = MutableLiveData(MediaState(track = track))
    val state: LiveData<MediaState> = mediaStateLD

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private var updateTimeRunnable: Runnable? = null

    init {
        preparePlayer()
    }

    private fun preparePlayer() {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(track.previewUrl)
            setOnPreparedListener {
                mediaStateLD.value = mediaStateLD.value?.copy(playbackState = PlaybackState.PREPARED)
            }
            setOnCompletionListener {
                stopProgressUpdates()
                mediaStateLD.value = mediaStateLD.value?.copy(
                    playbackState = PlaybackState.DEFAULT,
                    currentTime = "00:00"
                )
                resetPlayer()
            }
            prepareAsync()
        }
    }

    private fun resetPlayer() {
        mediaPlayer?.reset()
        mediaPlayer?.setDataSource(track.previewUrl)
        mediaPlayer?.prepareAsync()
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
        mediaPlayer?.start()
        mediaStateLD.value = mediaStateLD.value?.copy(playbackState = PlaybackState.PLAYING)
        startProgressUpdates()
    }

    private fun pausePlayer() {
        if (mediaStateLD.value?.playbackState == PlaybackState.PLAYING) {
            mediaPlayer?.pause()
            mediaStateLD.value = mediaStateLD.value?.copy(playbackState = PlaybackState.PAUSED)
            stopProgressUpdates()
        }
    }

    private fun startProgressUpdates() {
        updateTimeRunnable = object : Runnable {
            override fun run() {
                if (mediaStateLD.value?.playbackState == PlaybackState.PLAYING) {
                    val time = dateFormat.format(mediaPlayer?.currentPosition ?: 0)
                    mediaStateLD.value = mediaStateLD.value?.copy(currentTime = time)
                    handler.postDelayed(this, DELAY_PLAYBACK_TIME)
                }
            }
        }
        handler.post(updateTimeRunnable!!)
    }

    private fun stopProgressUpdates() {
        updateTimeRunnable?.let { handler.removeCallbacks(it) }
    }

    override fun onCleared() {
        super.onCleared()
        stopProgressUpdates()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    companion object {
        private const val DELAY_PLAYBACK_TIME = 400L
    }
}