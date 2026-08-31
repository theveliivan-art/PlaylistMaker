package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import android.os.Handler
import android.os.Looper
import android.media.MediaPlayer
import java.text.SimpleDateFormat
import java.util.Locale
class MediaActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT
    private lateinit var mediaPlayer: MediaPlayer
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateTimeRunnable: Runnable
    private lateinit var buttonPlay: ImageButton
    private lateinit var buttonPause: ImageButton
    private lateinit var trackPlaybackTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_media)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button_back_click = findViewById<ImageView>(R.id.media_button_back)
        button_back_click.setOnClickListener {
            finish()
        }

        val track = intent.getParcelableExtra<Track>("track") ?: return

        val trackImage = findViewById<ImageView>(R.id.media_track_image)
        val radiusInPx = applicationContext.resources.getDimensionPixelSize(R.dimen.media_track_image_radius)
        val requestOptions = RequestOptions()
            .transform(RoundedCorners(radiusInPx))
            .placeholder(R.drawable.ic_placeholder_312)
            .error(R.drawable.ic_placeholder_312)

        Glide.with(applicationContext)
            .load(track.getCoverArtwork())
            .apply(requestOptions)
            .into(trackImage)

        val trackName = findViewById<TextView>(R.id.media_track_name)
        trackName.text = track.trackName
        val trackArtist = findViewById<TextView>(R.id.media_track_artist)
        trackArtist.text = track.artistName
        val trackDuration = findViewById<TextView>(R.id.media_data_duration)
        trackDuration.text = track.trackTime
        val trackGenre = findViewById<TextView>(R.id.media_data_genre)
        trackGenre.text = track.primaryGenreName
        val trackCountry = findViewById<TextView>(R.id.media_data_country)
        trackCountry.text = track.country

        val trackAlbumInf = findViewById<TextView>(R.id.media_inf_album)
        val trackAlbumData = findViewById<TextView>(R.id.media_data_album)
        if (track.collectionName.isEmpty()){
            trackAlbumInf.isVisible = false
            trackAlbumData.isVisible = false
        }
        else{
            trackAlbumData.text = track.collectionName
            trackAlbumInf.isVisible = true
            trackAlbumData.isVisible = true
        }

        val trackYearInf = findViewById<TextView>(R.id.media_inf_year)
        val trackYearData = findViewById<TextView>(R.id.media_data_year)
        if (track.releaseDate.isEmpty()){
            trackYearInf.isVisible = false
            trackYearData.isVisible = false
        }
        else{
            trackYearData.text = extractYear(track.releaseDate)
            trackYearInf.isVisible = true
            trackYearData.isVisible = true
        }

        buttonPlay = findViewById<ImageButton>(R.id.buttonPlay)
        buttonPause = findViewById<ImageButton>(R.id.buttonPause)
        trackPlaybackTime = findViewById<TextView>(R.id.media_track_duration)

        buttonPlay.isVisible = true
        buttonPause.isVisible = false
        buttonPlay.isEnabled = false

        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            buttonPlay.isEnabled = true
            playerState = STATE_PREPARED
        }

        mediaPlayer.setOnCompletionListener {

            handler.removeCallbacks(updateTimeRunnable)
            trackPlaybackTime.text = "00:00"

            buttonPlay.isVisible = true
            buttonPause.isVisible = false
            buttonPlay.isEnabled = false
            playerState = STATE_DEFAULT

            mediaPlayer.reset()
            mediaPlayer.setDataSource(track.previewUrl)
            mediaPlayer.prepareAsync()
        }

        updateTimeRunnable = object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    val formatted = SimpleDateFormat("mm:ss", Locale.getDefault())
                        .format(mediaPlayer.currentPosition)
                    trackPlaybackTime.text = formatted
                    handler.postDelayed(this, 400)
                }
            }
        }

        buttonPlay.setOnClickListener {
            if (playerState == STATE_PREPARED || playerState == STATE_PAUSED) {
                startPlayer()
            }
        }

        buttonPause.setOnClickListener {
            if (playerState == STATE_PLAYING) {
                pausePlayer()
            }
        }

    }
    private fun extractYear(releaseDate: String): String? {
        return releaseDate.substringBefore("-").takeIf { it.length == 4 }
    }
    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        buttonPlay.isVisible = false
        buttonPause.isVisible = true
        handler.removeCallbacks(updateTimeRunnable)
        handler.post(updateTimeRunnable)
    }
    private fun pausePlayer() {
        if (playerState == STATE_PLAYING) {
            mediaPlayer.pause()
            playerState = STATE_PAUSED
            buttonPlay.isVisible = true
            buttonPause.isVisible = false
            handler.removeCallbacks(updateTimeRunnable)
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeRunnable)
        mediaPlayer.release()
    }

}