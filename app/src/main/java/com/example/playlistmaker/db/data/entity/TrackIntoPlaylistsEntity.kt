package com.example.playlistmaker.db.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracks_into_playlists_table",
    indices = [Index(value = ["trackId"], unique = true)]
)
data class TrackIntoPlaylistsEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trackId: Long,
    val artworkUrl100: String,
    val trackName: String,
    val artistName: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val trackTimeMillis: Long,
    val previewUrl: String?,
    var isFavorite: Boolean
)