package com.example.playlistmaker.db.domain.impl

import com.example.playlistmaker.db.domain.api.FavoriteTrackInteractor
import com.example.playlistmaker.db.domain.api.FavoriteTrackRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTrackInteractorImpl(
    private val favoriteTrackRepository: FavoriteTrackRepository
): FavoriteTrackInteractor {
    override suspend fun addFavoriteTrack(track: Track) {
        return favoriteTrackRepository.addFavoriteTrack(track)
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        return favoriteTrackRepository.deleteFavoriteTrack(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTrackRepository.getFavoriteTracks().map { tracks ->
            tracks.reversed()
        }
    }

    override fun getFavoriteTrackId(): Flow<List<Track>> {
        return favoriteTrackRepository.getFavoriteTracks()
    }

    override suspend fun isTrackFavorite(trackId: Long): Boolean {
        return favoriteTrackRepository.isTrackFavorite(trackId)
    }

}