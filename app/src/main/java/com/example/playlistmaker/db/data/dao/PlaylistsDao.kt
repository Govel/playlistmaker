package com.example.playlistmaker.db.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.db.data.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {
    @Query("SELECT * FROM playlists_table")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlists_table WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Int)

    @Query("SELECT * FROM playlists_table WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: Int): PlaylistEntity
}