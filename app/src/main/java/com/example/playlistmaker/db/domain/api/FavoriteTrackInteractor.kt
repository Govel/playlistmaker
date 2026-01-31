package com.example.playlistmaker.db.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTrackInteractor {
    suspend fun addFavoriteTrack(track: Track)
    suspend fun deleteFavoriteTrack(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
    fun getFavoriteTrackId(): Flow<List<Track>>
    suspend fun isTrackFavorite(trackId: Long): Boolean
}
