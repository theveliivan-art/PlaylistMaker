package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class MediaActivity : AppCompatActivity() {
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
            trackAlbumInf.visibility = View.GONE
            trackAlbumData.visibility = View.GONE
        }
        else{
            trackAlbumData.text = track.collectionName
            trackAlbumInf.visibility = View.VISIBLE
            trackAlbumData.visibility = View.VISIBLE
        }

        val trackYearInf = findViewById<TextView>(R.id.media_inf_year)
        val trackYearData = findViewById<TextView>(R.id.media_data_year)
        if (track.releaseDate.isEmpty()){
            trackYearInf.visibility = View.GONE
            trackYearData.visibility = View.GONE
        }
        else{
            trackYearData.text = extractYear(track.releaseDate)
            trackYearInf.visibility = View.VISIBLE
            trackYearData.visibility = View.VISIBLE
        }
    }
    private fun extractYear(releaseDate: String): String? {
        return releaseDate.substringBefore("-").takeIf { it.length == 4 }
    }
}