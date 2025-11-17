package com.example.photo.data.repository

import com.example.photo.core.CacheTracker
import com.example.photo.data.local.PhotoDao
import com.example.photo.data.remote.UnsplashApi
import com.example.photo.domain.model.CacheStatus
import com.example.photo.domain.model.Photo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultPhotoRepository(
    private val unsplashApi: UnsplashApi,
    private val photoDao: PhotoDao,
    private val cacheTracker: CacheTracker,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : PhotoRepository {

    override suspend fun getLatestPhotos(forceRefresh: Boolean): Result<List<Photo>> =
        withContext(dispatcher) {
            runCatching {
                val favoriteIds = photoDao.getFavoriteIds().toSet()
                val cachedPhotos = photoDao.getPhotos().map { it.toDomain(favoriteIds.contains(it.id)) }
                if (cachedPhotos.isNotEmpty() && !forceRefresh) {
                    return@runCatching cachedPhotos
                }
                try {
                    val latestFavoriteIds = photoDao.getFavoriteIds().toSet()
                    val remotePhotos = unsplashApi.getLatestPhotos(
                        page = 1,
                        perPage = PAGE_SIZE,
                        orderBy = DEFAULT_ORDER
                    ).mapIndexed { index, remote ->
                        remote.toDomain(index, latestFavoriteIds.contains(remote.id))
                    }

                    photoDao.clearPhotos()
                    photoDao.insertPhotos(
                        remotePhotos.mapIndexed { index, photo ->
                            photo.toEntity(index)
                        }
                    )
                    cacheTracker.markUpdated()
                    remotePhotos
                } catch (error: Exception) {
                    if (cachedPhotos.isNotEmpty()) {
                        cachedPhotos
                    } else {
                        throw error
                    }
                }
            }
        }

    override suspend fun searchPhotos(query: String): Result<List<Photo>> =
        withContext(dispatcher) {
            runCatching {
                // 1. Grab current favorites to render heart state correctly
                val favoriteIds = photoDao.getFavoriteIds().toSet()

                // 2. Call the API search endpoint
                val response = unsplashApi.searchPhotos(
                    query = query,
                    page = 1,
                    perPage = PAGE_SIZE
                )

                // 3. Map to domain models
                // Note: search results are not cached to DB (keeps the main feed untouched),
                // we just return the live data and flag favorites on the fly.
                response.results.mapIndexed { index, remote ->
                    remote.toDomain(index, favoriteIds.contains(remote.id))
                }
            }
        }

    override suspend fun getPhoto(photoId: String): Result<Photo> =
        withContext(dispatcher) {
            runCatching {
                val cached = photoDao.getPhoto(photoId)
                if (cached != null) {
                    val isFavorite = photoDao.isFavorite(photoId)
                    return@runCatching cached.toDomain(isFavorite)
                }
                val favorite = photoDao.getFavoritePhoto(photoId)
                favorite?.toDomain() ?: throw IllegalStateException("Photo not found locally")
            }
        }

    override suspend fun getFavoritePhotos(): Result<List<Photo>> =
        withContext(dispatcher) {
            runCatching {
                photoDao.getFavoritePhotos().map { it.toDomain() }
            }
        }

    override suspend fun toggleFavorite(photo: Photo): Result<Boolean> =
        withContext(dispatcher) {
            runCatching {
                val currentlyFavorite = photoDao.isFavorite(photo.id)
                if (currentlyFavorite) {
                    photoDao.deleteFavorite(photo.id)
                    false
                } else {
                    photoDao.insertFavorite(photo.toFavoriteEntity(System.currentTimeMillis()))
                    true
                }
            }
        }

    override suspend fun isFavorite(photoId: String): Result<Boolean> =
        withContext(dispatcher) {
            runCatching { photoDao.isFavorite(photoId) }
        }

    override suspend fun getCacheStatus(): Result<CacheStatus> =
        withContext(dispatcher) {
            runCatching {
                CacheStatus(
                    cachedPhotos = photoDao.countPhotos(),
                    lastSyncTimestamp = cacheTracker.lastUpdateTimestamp()
                )
            }
        }

    override suspend fun clearCache(): Result<Unit> =
        withContext(dispatcher) {
            runCatching {
                photoDao.clearPhotos()
                cacheTracker.clear()
            }
        }

    private companion object {
        const val PAGE_SIZE = 30
        const val DEFAULT_ORDER = "latest"
    }
}
