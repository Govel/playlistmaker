package com.example.playlistmaker.media.playlists.new_playlist.domain.model

data class Playlist(
    val id: Int,
    val name: String,
    val description: String,
    val imgPath: String,
    var tracksList: String,
    var totalTracks: Int
)