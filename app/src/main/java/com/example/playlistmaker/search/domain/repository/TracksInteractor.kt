package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.consumer.Consumer
import com.example.playlistmaker.search.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: Consumer)
    fun saveTrackToHistory(track: Track)
    fun loadTracksFromHistory(): List<Track>
    fun clearHistory()
}
