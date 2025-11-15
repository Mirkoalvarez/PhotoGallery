package com.example.photo.domain.model

data class Photo(
    val id: String,
    val title: String,
    val description: String,
    val thumbUrl: String,
    val fullUrl: String,
    val authorName: String,
    val likes: Int,
    val width: Int,
    val height: Int,
    val isFavorite: Boolean
) {
    val resolutionText: String
        get() = "${width}x$height"
}
