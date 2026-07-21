package com.practicum.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter( private val tracks: List<Track>,
                    private val historySearch: ArrayList<Track>,
                    private val onHistoryChangeListener: OnHistoryChangeListener? = null
                    ): RecyclerView.Adapter<TrackViewHolder>() {

    interface OnHistoryChangeListener {
        fun onHistoryChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            addTrackToHistory(tracks[position])
        }
    }

    override fun getItemCount(): Int = tracks.size

    fun addTrackToHistory(newTrack: Track){

        for(historyTrack in historySearch){
           if(newTrack.trackId==historyTrack.trackId){
               historySearch.remove(historyTrack)
               break
           }
        }
        historySearch.addFirst(newTrack)
        while (historySearch.size>10){
            historySearch.removeLast()
        }
        onHistoryChangeListener?.onHistoryChanged()
    }

}