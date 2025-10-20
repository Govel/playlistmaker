package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.repository.HistoryRepository
import com.example.playlistmaker.domain.repository.TracksInteractor
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.models.Track
import java.util.concurrent.Executors

class TracksInteractorImpl (
    private val trackRepository: TracksRepository,
    private val historyRepository: HistoryRepository
) : TracksInteractor {
    private val executor = Executors.newCachedThreadPool()
    override fun searchTracks(
        expression: String,
        consumer: Consumer
    ) {
        executor.execute {
            consumer.consume(trackRepository.searchTracks(expression))
        }
    }

    override fun saveTrackToHistory(track: Track) {
        historyRepository.saveTrackToHistory(track)
    }

    override fun loadTracksFromHistory(): MutableList<Track> {
        return historyRepository.loadTracksFromHistory()
    }

    override fun clearHistory() {
        historyRepository.clearHistory()
    }
}