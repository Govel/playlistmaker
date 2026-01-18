package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.consumer.Consumer
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.repository.HistoryRepository
import com.example.playlistmaker.search.domain.repository.TracksInteractor
import com.example.playlistmaker.search.domain.repository.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.Executors

class TracksInteractorImpl(
    private val trackRepository: TracksRepository,
    private val historyRepository: HistoryRepository
) : TracksInteractor {
    override fun searchTracks(
        expression: String
    ) : Flow<Pair<List<Track>?, String?>> {
        executor.execute {
            consumer.consume(trackRepository.searchTracks(expression))
        }
        return trackRepository.searchTracks(expression).map {

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