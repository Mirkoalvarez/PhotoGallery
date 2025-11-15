package com.example.photo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val thumbUrl: String,
    val fullUrl: String,
    val authorName: String,
    val likes: Int,
    val width: Int,
    val height: Int,
    val position: Int
)
