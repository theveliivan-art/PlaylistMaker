package com.practicum.playlistmaker.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.domain.models.Track

class TrackAdapter(private var tracks: List<Track>,
                   private val clickListener: (Track) -> Unit):
    RecyclerView.Adapter<TrackViewHolder>() {

    fun submitList(newTracks: List<Track>) {
        if (tracks == newTracks) return
        tracks = newTracks
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener { clickListener(track) }
    }

    override fun getItemCount(): Int = tracks.size
}