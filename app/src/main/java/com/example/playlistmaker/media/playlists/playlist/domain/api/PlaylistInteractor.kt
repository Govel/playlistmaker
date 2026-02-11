package com.example.playlistmaker.media.playlists.playlist.domain.api

import com.example.playlistmaker.media.playlists.new_playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

interface PlaylistInteractor {
    fun shareLink(playlist: Playlist?, tracks: List<Track>)
}