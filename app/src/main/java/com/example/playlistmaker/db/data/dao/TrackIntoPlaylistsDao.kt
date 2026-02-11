package com.example.playlistmaker.db.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.db.data.entity.TrackIntoPlaylistsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackIntoPlaylistsDao {
    @Insert(entity = TrackIntoPlaylistsEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackIntoPlaylists(track: TrackIntoPlaylistsEntity)

    @Query("SELECT * FROM tracks_into_playlists_table")
    fun getTracks(): Flow<List<TrackIntoPlaylistsEntity>>

    @Query("DELETE FROM tracks_into_playlists_table WHERE trackId = :trackId")
    suspend fun deleteTracks(trackId: Long)
}