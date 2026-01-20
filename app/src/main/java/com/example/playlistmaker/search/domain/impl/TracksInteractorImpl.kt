package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.repository.HistoryRepository
import com.example.playlistmaker.search.domain.repository.TracksInteractor
import com.example.playlistmaker.search.domain.repository.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(
    private val trackRepository: TracksRepository,
    private val historyRepository: HistoryRepository
) : TracksInteractor {
    override fun searchTracks(
        expression: String
    ) : Flow<Pair<List<Track>?, String?>> {
        return trackRepository.searchTracks(expression).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
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