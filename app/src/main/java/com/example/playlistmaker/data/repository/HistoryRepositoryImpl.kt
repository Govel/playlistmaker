package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.TrackHistory
import com.example.playlistmaker.data.mapper.TrackHistoryMapper
import com.example.playlistmaker.data.storages.local.SharedPrefsClient
import com.example.playlistmaker.domain.repository.HistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import kotlin.collections.removeAll

class HistoryRepositoryImpl (
    private val prefs: SharedPrefsClient<String>,
    private val gson: Gson = Gson(),
) : HistoryRepository {

    override fun loadTracksFromHistory(): MutableList<Track> {
        val json = prefs.load("")
        return TrackHistoryMapper.mapListToDomain(
            try {
                gson.fromJson(json, Array<TrackHistory>::class.java).toMutableList()
            } catch (_: Exception) {
                mutableListOf()
            }
        ).toMutableList()
    }

    override fun clearHistory() {
        prefs.clear(KEY_TRACKS)
    }

    override fun saveAllTracksToHistory(tracks: List<Track>) {
        val json = gson.toJson(TrackHistoryMapper.mapListToHistory(tracks))
        prefs.save(json)
    }

    override fun saveTrackToHistory(track: Track) {
        val list = loadTracksFromHistory()
        list.removeAll { it.trackId == track.trackId }
        list.add(0, track)
        if (list.size > MAX_SIZE) {
            list.removeAt(list.size - 1)
        }
        saveAllTracksToHistory(list)
    }

    companion object {
        private const val KEY_TRACKS = "tracks"
        private const val MAX_SIZE = 10
    }
}