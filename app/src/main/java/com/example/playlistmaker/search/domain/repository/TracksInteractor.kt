package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>>
    fun saveTrackToHistory(track: Track)
    fun loadTracksFromHistory(): List<Track>
    fun clearHistory()
}
