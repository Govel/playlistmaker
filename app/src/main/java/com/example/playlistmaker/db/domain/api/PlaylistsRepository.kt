package com.example.playlistmaker.db.domain.api

import com.example.playlistmaker.media.playlists.new_playlist.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist, track: Track, tracksList: MutableList<Long>)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun addTrackIntoPlaylists(track: Track)
}