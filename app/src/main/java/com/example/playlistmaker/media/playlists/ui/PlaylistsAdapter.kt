package com.example.playlistmaker.media.playlists.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.media.playlists.new_playlist.domain.api.ExternalInteractor
import com.example.playlistmaker.media.playlists.new_playlist.domain.models.Playlist

class PlaylistsAdapter(
    private val playlists: List<Playlist>,
    private val externalInteractor: ExternalInteractor,
    private val onItemClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistsViewHolder = PlaylistsViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: PlaylistsViewHolder,
        position: Int
    ) {
        val playlist = playlists[position]
        holder.bind(playlist, externalInteractor.getUriByCoverName(playlist.imgPath))
        holder.itemView.setOnClickListener {
            onItemClick(playlist)
        }
    }

    override fun getItemCount(): Int = playlists.size

}