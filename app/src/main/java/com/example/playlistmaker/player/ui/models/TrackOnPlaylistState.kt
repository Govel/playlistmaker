package com.example.playlistmaker.player.ui.models

import com.example.playlistmaker.media.playlists.new_playlist.domain.model.Playlist

sealed interface TrackOnPlaylistState {
    object Empty: TrackOnPlaylistState
    data class Content(val playlists: List<Playlist>): TrackOnPlaylistState
}