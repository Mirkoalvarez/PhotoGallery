package com.example.photo.data.remote.model

import com.squareup.moshi.Json

data class RemotePhoto(
    val id: String,
    val description: String?,
    @Json(name = "alt_description") val altDescription: String?,
    val likes: Int,
    val width: Int,
    val height: Int,
    @Json(name = "created_at") val createdAt: String?,
    val urls: RemotePhotoUrls,
    val user: RemoteUser
)

data class RemotePhotoUrls(
    val thumb: String,
    val small: String,
    val regular: String,
    val full: String
)

data class RemoteUser(
    val name: String
)
