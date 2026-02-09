package com.example.playlistmaker.db.domain.impl

import com.example.playlistmaker.db.domain.api.PlaylistsInteractor
import com.example.playlistmaker.db.domain.api.PlaylistsRepository
import com.example.playlistmaker.media.playlists.new_playlist.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlin.Long

class PlaylistsInteractorImpl(private val playlistsRepository: PlaylistsRepository) : PlaylistsInteractor {
    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getPlaylists()
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        return playlistsRepository.addPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist, track: Track, tracksList: MutableList<Long>) {
        return playlistsRepository.updatePlaylist(playlist, track, tracksList)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        return playlistsRepository.deletePlaylist(playlist)
    }

    override suspend fun addTrackIntoPlaylists(track: Track) {
        return playlistsRepository.addTrackIntoPlaylists(track)
    }
}