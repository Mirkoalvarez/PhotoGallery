package com.example.photo.data.repository

import com.example.photo.domain.model.CacheStatus
import com.example.photo.domain.model.Photo

interface PhotoRepository {
    suspend fun getLatestPhotos(forceRefresh: Boolean = false): Result<List<Photo>>
    suspend fun getPhoto(photoId: String): Result<Photo>
    suspend fun getFavoritePhotos(): Result<List<Photo>>
    suspend fun toggleFavorite(photo: Photo): Result<Boolean>
    suspend fun isFavorite(photoId: String): Result<Boolean>
    suspend fun getCacheStatus(): Result<CacheStatus>
    suspend fun clearCache(): Result<Unit>

    suspend fun searchPhotos(query: String): Result<List<Photo>>
}
