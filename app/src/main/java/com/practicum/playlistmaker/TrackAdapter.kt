package com.practicum.playlistmaker

import android.content.Intent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter( private val tracks: List<Track>,
                    private val historySearch: MutableList<Track>,
                    private val onHistoryChangeListener: OnHistoryChangeListener? = null
                    ): RecyclerView.Adapter<TrackViewHolder>() {

    interface OnHistoryChangeListener {
        fun onHistoryChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            addTrackToHistory(track)
            val context = holder.itemView.context
            val intent = Intent(context, MediaActivity::class.java).apply {
                putExtra("track", track)
            }
            context.startActivity(intent)
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
        historySearch.add(0, newTrack)
        while (historySearch.size>COUNT_TRACK_IN_HISTORY){
            historySearch.removeAt(historySearch.size-1)
        }
        onHistoryChangeListener?.onHistoryChanged()
    }

    companion object {
        const val COUNT_TRACK_IN_HISTORY = 10
    }

}