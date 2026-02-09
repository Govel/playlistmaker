package com.example.playlistmaker.db.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.db.data.dao.FavoriteTrackDao
import com.example.playlistmaker.db.data.dao.PlaylistsDao
import com.example.playlistmaker.db.data.dao.TrackIntoPlaylistsDao
import com.example.playlistmaker.db.data.entity.FavoriteTrackEntity
import com.example.playlistmaker.db.data.entity.PlaylistEntity
import com.example.playlistmaker.db.data.entity.TrackIntoPlaylistsEntity


@Database(version = 1, entities = [FavoriteTrackEntity::class, PlaylistEntity::class, TrackIntoPlaylistsEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTrackDao(): FavoriteTrackDao

    abstract fun playlistsDao(): PlaylistsDao

    abstract fun trackIntoPlaylistsDao(): TrackIntoPlaylistsDao
}