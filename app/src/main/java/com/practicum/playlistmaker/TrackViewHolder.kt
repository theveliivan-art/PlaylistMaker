package com.practicum.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(trackView: ViewGroup): RecyclerView.ViewHolder( LayoutInflater.from(trackView.context).inflate(R.layout.item_track, trackView, false)) {
    private val trackImage: ImageView = itemView.findViewById(R.id.trackImage)
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)

    fun bind(track: Track){
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTime

        Glide.with(itemView.context)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_media_24)
            .transform(RoundedCorners(2))
            .into(trackImage)

    }
}