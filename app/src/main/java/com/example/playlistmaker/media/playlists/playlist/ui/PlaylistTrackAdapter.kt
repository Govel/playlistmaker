package com.example.playlistmaker.media.playlists.playlist.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackViewHolder

class PlaylistTrackAdapter(
    private val tracks: List<Track>,
    private val onItemLongClick: (Track) -> Boolean,
    private val onItemClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder.from(parent)

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onItemClick(track)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(track)
        }
    }
}
