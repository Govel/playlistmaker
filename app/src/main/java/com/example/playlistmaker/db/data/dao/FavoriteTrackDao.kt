package com.example.playlistmaker.db.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.db.data.entity.FavoriteTrackEntity

@Dao
interface FavoriteTrackDao {
    @Insert(entity = FavoriteTrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteTrack(favoriteTrack: FavoriteTrackEntity)

    @Query("SELECT * FROM favorite_tracks_table")
    suspend fun getFavoriteTracks(): List<FavoriteTrackEntity>

    @Query("SELECT id FROM favorite_tracks_table")
    suspend fun getFavoriteTrackId(): List<FavoriteTrackEntity>

    @Delete(entity = FavoriteTrackEntity::class)
    suspend fun deleteFavoriteTrack(favoriteTrack: FavoriteTrackEntity)
}