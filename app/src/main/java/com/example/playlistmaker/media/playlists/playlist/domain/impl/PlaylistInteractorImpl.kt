package com.example.playlistmaker.media.playlists.playlist.domain.impl

import com.example.playlistmaker.media.playlists.new_playlist.domain.models.Playlist
import com.example.playlistmaker.media.playlists.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.playlists.playlist.domain.api.PlaylistRepository
import com.example.playlistmaker.search.domain.models.Track

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
): PlaylistInteractor {
    override fun shareLink(playlist: Playlist?, tracks: List<Track>) {
        playlistRepository.shareLink(playlist,tracks)
    }

}