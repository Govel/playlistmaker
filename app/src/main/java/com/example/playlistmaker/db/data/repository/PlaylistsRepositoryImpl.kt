package com.example.playlistmaker.db.data.repository

import com.example.playlistmaker.db.data.converter.PlaylistDbConverter
import com.example.playlistmaker.db.data.converter.TrackIntoPlaylistsDbConverter
import com.example.playlistmaker.db.data.dao.PlaylistsDao
import com.example.playlistmaker.db.data.dao.TrackIntoPlaylistsDao
import com.example.playlistmaker.db.data.entity.PlaylistEntity
import com.example.playlistmaker.db.data.entity.TrackIntoPlaylistsEntity
import com.example.playlistmaker.db.domain.api.PlaylistsRepository
import com.example.playlistmaker.media.playlists.new_playlist.domain.models.Playlist
import com.example.playlistmaker.player.ui.models.TrackIds
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistsDao.updatePlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun updateTrackIntoPlaylist(
        playlist: Playlist,
        track: Track,
        tracksList: MutableList<Long>,
        isDelete: Boolean
    ) {
        if (!tracksList.contains(track.trackId) && !isDelete)  {
            updatePlaylistTrackList(playlist, track, tracksList, false)
            playlist.totalTracks += 1
        } else if (tracksList.contains(track.trackId) && isDelete) {
            updatePlaylistTrackList(playlist, track, tracksList, true)
            playlist.totalTracks -= 1
        }
        val playlistEntity = convertToPlaylistEntity(playlist)
        playlistsDao.updatePlaylist(playlistEntity)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        playlistsDao.deletePlaylist(playlistId)
    }

    override suspend fun addTrackIntoPlaylists(track: Track) {
        val trackIntoPlaylistsEntity = convertToTrackToPlaylistEntity(track)
        trackIntoPlaylistsDao.insertTrackIntoPlaylists(trackIntoPlaylistsEntity)
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist {
        return playlistDbConverter.map(playlistsDao.getPlaylistById(playlistId))
    }

    fun getTracksIntoPlaylist(): Flow<List<TrackIntoPlaylistsEntity>> =
        trackIntoPlaylistsDao.getTracks()


    override fun getTracks(trackList: String): Flow<List<Track>> {
        val idList = trackListIdToList(trackList)
        return getTracksIntoPlaylist().map { trackEntities ->
            val mapById = trackEntities.associateBy { it.trackId }
            idList.mapNotNull { id ->
                mapById[id]?.let { entity -> convertToTrack(entity) }
            }
        }
    }

    override suspend fun deleteTrack(track: Track) {
        trackIntoPlaylistsDao.deleteTracks(track.trackId)
    }

    override suspend fun isTrackUsedInAnyPlaylist(trackId: Long): Boolean {
        val allPlaylists = playlistsDao.getPlaylists().first()
        for (playlistEntity in allPlaylists) {
            val trackIds = trackListIdToList(playlistEntity.tracksList)
            if (trackId in trackIds) {
                return true
            }
        }
        return false
    }

    private fun convertToTrackToPlaylistEntity(track: Track): TrackIntoPlaylistsEntity {
        return trackIntoPlaylistsDbConverter.map(track)
    }
    private fun convertToPlaylistEntity(playlist: Playlist): PlaylistEntity {
        return playlistDbConverter.map(playlist)
    }
    private fun convertToTrack(trackEntity: TrackIntoPlaylistsEntity) : Track {
        return Track(
            trackEntity.trackId,
            trackEntity.trackName,
            trackEntity.artistName,
            trackEntity.trackTimeMillis,
            trackEntity.artworkUrl100,
            trackEntity.previewUrl,
            trackEntity.collectionName,
            trackEntity.releaseDate,
            trackEntity.primaryGenreName,
            trackEntity.country,
            trackEntity.isFavorite)
    }

    private fun updatePlaylistTrackList(
        playlist: Playlist,
        track: Track,
        trackList: MutableList<Long>,
        isDelete: Boolean
    ) {
        if (isDelete) {
            trackList.remove(track.trackId)
        } else {
            trackList.add(0, track.trackId)
        }

        val json = Gson().toJson(TrackIds(trackList))
        playlist.tracksList = json
    }

    private fun trackListIdToList(trackList: String) : List<Long> {
        if (trackList.isBlank()) return emptyList()
        return try {
            val trackIds = Gson().fromJson(trackList, TrackIds::class.java)
            trackIds?.trackIds ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}