package com.practicum.playlistmaker.presentation

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.domain.models.Track

class TrackAdapter(private val tracks: List<Track>,
                   private val clickListener: (Track) -> Unit):RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener { clickListener(track) }
    }

    override fun getItemCount(): Int = tracks.size
}