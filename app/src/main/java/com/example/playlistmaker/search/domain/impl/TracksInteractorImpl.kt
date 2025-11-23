package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.consumer.Consumer
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.repository.HistoryRepository
import com.example.playlistmaker.search.domain.repository.TracksInteractor
import com.example.playlistmaker.search.domain.repository.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(
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