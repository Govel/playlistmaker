package com.example.playlistmaker.domain.repository.tracks

import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: Consumer)
    fun saveTrackToHistory(track: Track)
    fun loadTracksFromHistory(): List<Track>
    fun clearHistory()
}
