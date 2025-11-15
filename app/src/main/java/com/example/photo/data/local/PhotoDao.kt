package com.example.photo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhotoDao {

    @Query("SELECT * FROM photos ORDER BY position ASC")
    suspend fun getPhotos(): List<PhotoEntity>

    @Query("SELECT * FROM photos WHERE id = :photoId LIMIT 1")
    suspend fun getPhoto(photoId: String): PhotoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PhotoEntity>)

    @Query("DELETE FROM photos")
    suspend fun clearPhotos()

    @Query("SELECT COUNT(*) FROM photos")
    suspend fun countPhotos(): Int

    @Query("SELECT id FROM favorite_photos")
    suspend fun getFavoriteIds(): List<String>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_photos WHERE id = :photoId)")
    suspend fun isFavorite(photoId: String): Boolean

    @Query("SELECT * FROM favorite_photos ORDER BY savedAt DESC")
    suspend fun getFavoritePhotos(): List<FavoritePhotoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(photo: FavoritePhotoEntity)

    @Query("DELETE FROM favorite_photos WHERE id = :photoId")
    suspend fun deleteFavorite(photoId: String)

    @Query("SELECT * FROM favorite_photos WHERE id = :photoId LIMIT 1")
    suspend fun getFavoritePhoto(photoId: String): FavoritePhotoEntity?
}
