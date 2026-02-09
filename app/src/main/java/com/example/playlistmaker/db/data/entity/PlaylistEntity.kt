package com.example.playlistmaker.db.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlists_table"
)
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val description: String,
    val imgPath: String,
    val tracksList: String,
    val totalTracks: Int
)