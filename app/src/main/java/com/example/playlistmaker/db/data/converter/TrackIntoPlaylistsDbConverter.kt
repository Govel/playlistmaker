package com.example.playlistmaker.db.data.converter

import com.example.playlistmaker.db.data.entity.TrackIntoPlaylistsEntity
import com.example.playlistmaker.search.domain.models.Track

class TrackIntoPlaylistsDbConverter {
    fun map(track: Track): TrackIntoPlaylistsEntity {
        return TrackIntoPlaylistsEntity(0, track.trackId, track.artworkUrl100, track.trackName, track.artistName, track.collectionName, track.releaseDate, track.primaryGenreName, track.country, track.trackTimeMillis, track.previewUrl)
    }

    fun map(track: TrackIntoPlaylistsEntity): Track {
        return Track(track.trackId, track.trackName, track.artistName, track.trackTimeMillis, track.artworkUrl100, track.previewUrl, track.collectionName, track.releaseDate, track.primaryGenreName, track.country)
    }
}