package com.example.photo.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResponse(
    // Important: reuse RemotePhoto since it already exists
    @Json(name = "results") val results: List<RemotePhoto>,
    @Json(name = "total") val total: Int,
    @Json(name = "total_pages") val totalPages: Int
)
