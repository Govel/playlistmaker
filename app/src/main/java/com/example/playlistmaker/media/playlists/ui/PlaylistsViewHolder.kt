package com.example.playlistmaker.media.playlists.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.playlistmaker.R
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.RvPlaylistsBinding
import com.example.playlistmaker.media.playlists.new_playlist.domain.models.Playlist
import com.example.playlistmaker.util.FormatTracksCount
import com.example.playlistmaker.util.LocalUtils

class PlaylistsViewHolder(private val binding: RvPlaylistsBinding): RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(data: Playlist?, coverUri: Uri?) {
        binding.title.text = data?.name
        binding.totalTracks.text = itemView.resources.getQuantityString(R.plurals.tracks,
            data?.totalTracks ?: 0, data?.totalTracks ?: 0)
        Glide.with(binding.root)
            .load(coverUri)
            .placeholder(R.drawable.placeholder_cover)
            .transform(CenterCrop(), RoundedCorners(LocalUtils().dpToPx(8f, itemView)))
            .into(binding.ivPlaylistCover)

    }

    companion object {
        fun from(parent: ViewGroup): PlaylistsViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = RvPlaylistsBinding.inflate(inflater, parent, false)
            return PlaylistsViewHolder(binding)
        }
    }
}