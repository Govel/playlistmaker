package com.example.playlistmaker.db.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTrackRepository {
    fun addFavoriteTrack(track: Track): Flow<Unit>
    fun deleteFavoriteTrack(track: Track): Flow<Unit>
    fun getFavoriteTracks(): Flow<List<Track>>
    fun getFavoriteTracksId(): Flow<List<Track>>
}