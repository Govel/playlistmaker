package com.example.playlistmaker.domain.impl.tracks

import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.tracks.HistoryRepository
import com.example.playlistmaker.domain.repository.tracks.TracksInteractor
import com.example.playlistmaker.domain.repository.tracks.TracksRepository
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