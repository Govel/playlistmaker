package com.example.playlistmaker.db.data.repository

import com.example.playlistmaker.db.data.convertor.FavoriteTrackDbConvertor
import com.example.playlistmaker.db.data.dao.FavoriteTrackDao
import com.example.playlistmaker.db.data.entity.FavoriteTrackEntity
import com.example.playlistmaker.db.domain.api.FavoriteTrackRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FavoriteTrackRepositoryImpl(
    private val dao: FavoriteTrackDao,
    private val trackDbConvertor: FavoriteTrackDbConvertor,
) : FavoriteTrackRepository {
    override suspend fun addFavoriteTrack(track: Track) {
        val favoriteTrack = convertToTrackEntity(track)
        dao.insertFavoriteTrack(favoriteTrack)
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        dao.deleteFavoriteTrackByTrackId(track.trackId)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> =
        dao.getFavoriteTracks()
            .map { entities -> entities.map { trackDbConvertor.map(it) } }

    override fun getFavoriteTracksId(): Flow<List<Track>> = flow {
        val favoriteTracks = dao.getFavoriteTrackId()
        emit(convertFromTrackEntity(tracks = favoriteTracks))
    }

    private fun convertFromTrackEntity(tracks: List<FavoriteTrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

    private fun convertToTrackEntity(track: Track): FavoriteTrackEntity {
        return trackDbConvertor.map(track)
    }

    override suspend fun isTrackFavorite(trackId: Long): Boolean {
        return dao.isTrackFavorite(trackId)
    }
}