package com.example.playlistmaker.db.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.playlistmaker.db.data.entity.TrackIntoPlaylistsEntity

@Dao
interface TrackIntoPlaylistsDao {
    @Insert(entity = TrackIntoPlaylistsEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackIntoPlaylists(track: TrackIntoPlaylistsEntity)
}