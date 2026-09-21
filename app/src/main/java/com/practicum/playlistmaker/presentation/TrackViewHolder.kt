package com.practicum.playlistmaker.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track

class TrackViewHolder(trackView: ViewGroup): RecyclerView.ViewHolder( LayoutInflater.from(trackView.context).inflate(
    R.layout.item_track, trackView, false)) {
    private val trackImage: ImageView = itemView.findViewById(R.id.trackImage)
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)

    fun bind(track: Track){
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTime

        val radiusInPx = itemView.resources.getDimensionPixelSize(R.dimen.search_track_image_radius)
        val requestOptions = RequestOptions()
            .transform(RoundedCorners(radiusInPx))
            .placeholder(R.drawable.ic_track_placeholder_45)
            .error(R.drawable.ic_track_placeholder_45)

        Glide.with(itemView.context)
            .load(track.artworkUrl100)
            .apply(requestOptions)
            .into(trackImage)

    }
}