package com.example.playlistmaker.media.playlists.ui.models

import com.example.playlistmaker.media.playlists.new_playlist.domain.models.Playlist

sealed interface PlaylistsState {
    object Empty: PlaylistsState
    data class Content(val playlists: List<Playlist>): PlaylistsState
}