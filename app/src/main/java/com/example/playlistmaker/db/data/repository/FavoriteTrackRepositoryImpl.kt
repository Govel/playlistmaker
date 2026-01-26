package com.example.playlistmaker.db.data.repository

import com.example.playlistmaker.db.data.AppDatabase
import com.example.playlistmaker.db.data.convertor.FavoriteTrackDbConvertor
import com.example.playlistmaker.db.data.entity.FavoriteTrackEntity
import com.example.playlistmaker.db.domain.api.FavoriteTrackRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTrackRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: FavoriteTrackDbConvertor,
) : FavoriteTrackRepository {
    override fun addFavoriteTrack(track: Track): Flow<Unit> = flow {
        val favoriteTrack = convertToTrackEntity(track)
        val insertFavoriteTrack = appDatabase.favoriteTrackDao().insertFavoriteTrack(favoriteTrack)
        emit(insertFavoriteTrack)
    }

    override fun deleteFavoriteTrack(track: Track): Flow<Unit> = flow {
        val favoriteTrack = convertToTrackEntity(track)
        val deleteFavoriteTrack = appDatabase.favoriteTrackDao().deleteFavoriteTrack(favoriteTrack)
        emit(deleteFavoriteTrack)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val favoriteTracks = appDatabase.favoriteTrackDao().getFavoriteTracks()
        emit(convertFromTrackEntity(tracks = favoriteTracks))
    }

    override fun getFavoriteTracksId(): Flow<List<Track>> = flow {
        val favoriteTracks = appDatabase.favoriteTrackDao().getFavoriteTrackId()
        emit(convertFromTrackEntity(tracks = favoriteTracks))
    }

    private fun convertFromTrackEntity(tracks: List<FavoriteTrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

    private fun convertToTrackEntity(track: Track): FavoriteTrackEntity {
        return trackDbConvertor.map(track)
    }

}