package com.example.playlistmaker.player.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.media.playlists.new_playlist.domain.api.ExternalInteractor
import com.example.playlistmaker.media.playlists.new_playlist.domain.models.Playlist

class BsPlaylistsAdapter(
    private val playlists: List<Playlist>,
    private val externalInteractor: ExternalInteractor,
    private val onItemClick: (Playlist) -> Unit
) : RecyclerView.Adapter<BsPlaylistsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BsPlaylistsViewHolder = BsPlaylistsViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: BsPlaylistsViewHolder,
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