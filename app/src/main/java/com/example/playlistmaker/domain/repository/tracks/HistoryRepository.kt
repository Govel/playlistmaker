package com.example.playlistmaker.domain.repository.tracks

import com.example.playlistmaker.domain.models.Track

interface HistoryRepository {
    fun saveTrackToHistory(track: Track)
    fun loadTracksFromHistory(): MutableList<Track>
    fun clearHistory()
    fun saveAllTracksToHistory(tracks: List<Track>)
}
