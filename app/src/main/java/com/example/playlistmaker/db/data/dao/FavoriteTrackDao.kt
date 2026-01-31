package com.example.playlistmaker.db.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.db.data.entity.FavoriteTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTrackDao {
    @Insert(entity = FavoriteTrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteTrack(favoriteTrack: FavoriteTrackEntity)

    @Query("SELECT * FROM favorite_tracks_table")
    fun getFavoriteTracks(): Flow<List<FavoriteTrackEntity>>

    @Query("SELECT * FROM favorite_tracks_table")
    fun getFavoriteTrackId(): List<FavoriteTrackEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_tracks_table WHERE trackId = :trackId)")
    suspend fun isTrackFavorite(trackId: Long): Boolean

    @Query("DELETE FROM favorite_tracks_table WHERE trackId = :trackId")
    suspend fun deleteFavoriteTrackByTrackId(trackId: Long)
}