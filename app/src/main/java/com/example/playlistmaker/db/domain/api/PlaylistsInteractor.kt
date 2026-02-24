package com.example.playlistmaker.db.domain.api

import com.example.playlistmaker.media.playlists.new_playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun updateTrackIntoPlaylist(playlist: Playlist, track: Track, tracksList: MutableList<Long>, isDelete: Boolean)
    suspend fun deletePlaylist(playlistId: Int)
    suspend fun addTrackIntoPlaylists(track: Track)
    suspend fun getPlaylistById(playlistId: Int): Playlist
    fun getTracks(trackList: String): Flow<List<Track>>
    suspend fun deleteTrack(track: Track)
    suspend fun isTrackUsedInAnyPlaylist(trackId: Long): Boolean
}