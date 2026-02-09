package com.example.playlistmaker.db.data.converter

import com.example.playlistmaker.db.data.entity.FavoriteTrackEntity
import com.example.playlistmaker.search.domain.models.Track

class FavoriteTrackDbConverter {
    fun map(track: Track): FavoriteTrackEntity {
        return FavoriteTrackEntity(0, track.trackId, track.artworkUrl100, track.trackName, track.artistName, track.collectionName, track.releaseDate, track.primaryGenreName, track.country, track.trackTimeMillis, track.previewUrl)
    }

    fun map(track: FavoriteTrackEntity): Track {
        return Track(track.trackId, track.trackName, track.artistName, track.trackTimeMillis, track.artworkUrl100, track.previewUrl, track.collectionName, track.releaseDate, track.primaryGenreName, track.country)
    }
}