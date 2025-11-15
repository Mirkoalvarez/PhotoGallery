package com.example.photo.domain.model

data class CacheStatus(
    val cachedPhotos: Int,
    val lastSyncTimestamp: Long
) {
    val hasCache: Boolean
        get() = cachedPhotos > 0
}
