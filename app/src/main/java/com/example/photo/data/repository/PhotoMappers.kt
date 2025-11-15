package com.example.photo.data.repository

import com.example.photo.data.local.FavoritePhotoEntity
import com.example.photo.data.local.PhotoEntity
import com.example.photo.data.remote.model.RemotePhoto
import com.example.photo.domain.model.Photo

fun RemotePhoto.toDomain(position: Int, isFavorite: Boolean = false): Photo = Photo(
    id = id,
    title = user.name,
    description = description ?: altDescription.orEmpty(),
    thumbUrl = urls.small,
    fullUrl = urls.full,
    authorName = user.name,
    likes = likes,
    width = width,
    height = height,
    isFavorite = isFavorite
)

fun PhotoEntity.toDomain(isFavorite: Boolean): Photo = Photo(
    id = id,
    title = title,
    description = description,
    thumbUrl = thumbUrl,
    fullUrl = fullUrl,
    authorName = authorName,
    likes = likes,
    width = width,
    height = height,
    isFavorite = isFavorite
)

fun Photo.toEntity(position: Int): PhotoEntity = PhotoEntity(
    id = id,
    title = title,
    description = description,
    thumbUrl = thumbUrl,
    fullUrl = fullUrl,
    authorName = authorName,
    likes = likes,
    width = width,
    height = height,
    position = position
)

fun FavoritePhotoEntity.toDomain(): Photo = Photo(
    id = id,
    title = title,
    description = description,
    thumbUrl = thumbUrl,
    fullUrl = fullUrl,
    authorName = authorName,
    likes = likes,
    width = width,
    height = height,
    isFavorite = true
)

fun Photo.toFavoriteEntity(timestamp: Long): FavoritePhotoEntity = FavoritePhotoEntity(
    id = id,
    title = title,
    description = description,
    thumbUrl = thumbUrl,
    fullUrl = fullUrl,
    authorName = authorName,
    likes = likes,
    width = width,
    height = height,
    savedAt = timestamp
)
