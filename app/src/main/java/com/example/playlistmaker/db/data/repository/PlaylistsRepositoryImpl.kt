package com.example.playlistmaker.db.data.repository

import com.example.playlistmaker.db.data.converter.PlaylistDbConverter
import com.example.playlistmaker.db.data.converter.TrackIntoPlaylistsDbConverter
import com.example.playlistmaker.db.data.dao.PlaylistsDao
import com.example.playlistmaker.db.data.dao.TrackIntoPlaylistsDao
import com.example.playlistmaker.db.data.entity.PlaylistEntity
import com.example.playlistmaker.db.data.entity.TrackIntoPlaylistsEntity
import com.example.playlistmaker.db.domain.api.PlaylistsRepository
import com.example.playlistmaker.media.playlists.new_playlist.domain.model.Playlist
import com.example.playlistmaker.player.ui.models.TrackIds
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val playlistsDao: PlaylistsDao,
    private val trackIntoPlaylistsDao: TrackIntoPlaylistsDao,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackIntoPlaylistsDbConverter: TrackIntoPlaylistsDbConverter
): PlaylistsRepository {
    override fun getPlaylists(): Flow<List<Playlist>> =
        playlistsDao.getPlaylists().map { entities -> entities.map { playlistDbConverter.map(it)} }

    override suspend fun addPlaylist(playlist: Playlist) {
        val playlistEntity = convertToPlaylistEntity(playlist)
        playlistsDao.insertPlaylist(playlistEntity)
    }

    override suspend fun updatePlaylist(
        playlist: Playlist,
        track: Track,
        tracksList: MutableList<Long>
    ) {
        if (!tracksList.contains(track.trackId))  {
            updatePlaylistTrackList(playlist, track, tracksList)
            playlist.totalTracks += 1
            val playlistEntity = convertToPlaylistEntity(playlist)
            playlistsDao.updatePlaylist(playlistEntity)
        }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val playlistEntity = convertToPlaylistEntity(playlist)
        playlistsDao.deletePlaylist(playlistEntity)
    }

    override suspend fun addTrackIntoPlaylists(track: Track) {
        val trackIntoPlaylistsEntity = convertToTrackToPlaylistEntity(track)
        trackIntoPlaylistsDao.insertTrackIntoPlaylists(trackIntoPlaylistsEntity)
    }

    private fun convertToTrackToPlaylistEntity(track: Track): TrackIntoPlaylistsEntity {
        return trackIntoPlaylistsDbConverter.map(track)
    }
    private fun convertToPlaylistEntity(playlist: Playlist): PlaylistEntity {
        return playlistDbConverter.map(playlist)
    }

    private fun updatePlaylistTrackList(
        playlist: Playlist,
        track: Track,
        trackList: MutableList<Long>
    ) {
        trackList.add(0, track.trackId)
        val json = Gson().toJson(TrackIds(trackList))
        playlist.tracksList = json
    }
}