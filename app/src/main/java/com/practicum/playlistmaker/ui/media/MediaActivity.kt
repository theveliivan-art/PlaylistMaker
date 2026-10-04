package com.practicum.playlistmaker.ui.media

import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.models.Track

class MediaActivity : AppCompatActivity() {

    private lateinit var viewModel: MediaViewModel
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

        val track = intent.getParcelableExtra<Track>(EXTRA_TRACK) ?: run {
            finish()
            return
        }

        viewModel = ViewModelProvider(
            this,
            MediaViewModelFactory(track, Creator.providePlayerInteractor())
        )[MediaViewModel::class.java]

        val button_back_click = findViewById<ImageView>(R.id.media_button_back)
        button_back_click.setOnClickListener { finish() }

        val trackImage = findViewById<ImageView>(R.id.media_track_image)
        val trackName = findViewById<TextView>(R.id.media_track_name)
        val trackArtist = findViewById<TextView>(R.id.media_track_artist)
        val trackDuration = findViewById<TextView>(R.id.media_data_duration)
        val trackGenre = findViewById<TextView>(R.id.media_data_genre)
        val trackCountry = findViewById<TextView>(R.id.media_data_country)
        val trackAlbumInf = findViewById<TextView>(R.id.media_inf_album)
        val trackAlbumData = findViewById<TextView>(R.id.media_data_album)
        val trackYearInf = findViewById<TextView>(R.id.media_inf_year)
        val trackYearData = findViewById<TextView>(R.id.media_data_year)

        buttonPlay = findViewById(R.id.buttonPlay)
        buttonPause = findViewById(R.id.buttonPause)
        trackPlaybackTime = findViewById(R.id.media_track_duration)

        val radiusInPx = applicationContext.resources.getDimensionPixelSize(R.dimen.media_track_image_radius)
        val requestOptions = RequestOptions()
            .transform(RoundedCorners(radiusInPx))
            .placeholder(R.drawable.ic_placeholder_312)
            .error(R.drawable.ic_placeholder_312)

        Glide.with(applicationContext)
            .load(track.getCoverArtwork())
            .apply(requestOptions)
            .into(trackImage)

        trackName.text = track.trackName
        trackArtist.text = track.artistName
        trackDuration.text = track.trackTime
        trackGenre.text = track.primaryGenreName
        trackCountry.text = track.country

        if (track.collectionName.isEmpty()) {
            trackAlbumInf.isVisible = false
            trackAlbumData.isVisible = false
        } else {
            trackAlbumData.text = track.collectionName
            trackAlbumInf.isVisible = true
            trackAlbumData.isVisible = true
        }

        if (track.releaseDate.isEmpty()) {
            trackYearInf.isVisible = false
            trackYearData.isVisible = false
        } else {
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

        buttonPlay.setOnClickListener { viewModel.onPlayClicked() }
        buttonPause.setOnClickListener { viewModel.onPauseClicked() }

        viewModel.state.observe(this) { state ->
            trackPlaybackTime.text = state.currentTime
            buttonPlay.isVisible = !state.isPlaying
            buttonPlay.isEnabled = state.isPlayEnabled
            buttonPause.isVisible = state.isPlaying
        }

    }

    private fun extractYear(releaseDate: String): String? =
        releaseDate.substringBefore("-").takeIf { it.length == 4 }

    override fun onPause() {
        super.onPause()
        viewModel.onScreenPaused()
    }

    companion object {
        const val EXTRA_TRACK = "track"
    }

}