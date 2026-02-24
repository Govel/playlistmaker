package com.example.playlistmaker.db.domain.impl

import com.example.playlistmaker.db.domain.api.PlaylistsInteractor
import com.example.playlistmaker.db.domain.api.PlaylistsRepository
import com.example.playlistmaker.media.playlists.new_playlist.domain.models.Playlist
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

    override suspend fun updatePlaylist(playlist: Playlist) {
        return playlistsRepository.updatePlaylist(playlist)
    }

    override suspend fun updateTrackIntoPlaylist(playlist: Playlist, track: Track, tracksList: MutableList<Long>, isDelete: Boolean) {
        return playlistsRepository.updateTrackIntoPlaylist(playlist, track, tracksList, isDelete)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        return playlistsRepository.deletePlaylist(playlistId)
    }

    override suspend fun addTrackIntoPlaylists(track: Track) {
        return playlistsRepository.addTrackIntoPlaylists(track)
    }

    override suspend fun getPlaylistById(playlistId: Int) : Playlist {
        return playlistsRepository.getPlaylistById(playlistId)
    }

    override fun getTracks(trackList: String): Flow<List<Track>> {
        return playlistsRepository.getTracks(trackList)
    }

    override suspend fun deleteTrack(track: Track) {
        return playlistsRepository.deleteTrack(track)
    }

    override suspend fun isTrackUsedInAnyPlaylist(trackId: Long): Boolean {
        return playlistsRepository.isTrackUsedInAnyPlaylist(trackId)
    }
}