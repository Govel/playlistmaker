package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.models.Track

interface HistoryRepository {
    fun saveTrackToHistory(track: Track)
    fun loadTracksFromHistory(): MutableList<Track>
    fun clearHistory()
    fun saveAllTracksToHistory(tracks: List<Track>)
}
