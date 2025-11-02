package com.example.playlistmaker.data.mapper

import com.example.playlistmaker.data.dto.TrackHistory
import com.example.playlistmaker.domain.models.Track

object TrackHistoryMapper {
    fun mapToHistory(model: Track): TrackHistory {
        return TrackHistory(
            trackId = model.trackId,
            trackName = model.trackName,
            artistName = model.artistName,
            trackTimeMillis = model.trackTimeMillis,
            artworkUrl100 = model.artworkUrl100,
            previewUrl = model.previewUrl,
            collectionName = model.collectionName,
            releaseDate = model.releaseDate,
            primaryGenreName = model.primaryGenreName,
            country = model.country
        )
    }

    fun mapListToHistory(models: List<Track>) = models.map {
        mapToHistory(it)
    }

    fun mapToDomain(item: TrackHistory): Track {
        return Track(
            trackId = item.trackId,
            trackName = item.trackName,
            artistName = item.artistName,
            trackTimeMillis = item.trackTimeMillis,
            artworkUrl100 = item.artworkUrl100,
            previewUrl = item.previewUrl,
            collectionName = item.collectionName,
            releaseDate = item.releaseDate,
            primaryGenreName = item.primaryGenreName,
            country = item.country
        )
    }

    fun mapListToDomain(items: List<TrackHistory>) = items.map {
        mapToDomain(it)
    }
}
