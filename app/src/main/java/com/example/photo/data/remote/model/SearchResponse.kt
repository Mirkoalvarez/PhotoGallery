package com.example.photo.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResponse(
    // CAMBIO IMPORTANTE: Usamos RemotePhoto, que es el que ya tenés creado
    @Json(name = "results") val results: List<RemotePhoto>,
    @Json(name = "total") val total: Int,
    @Json(name = "total_pages") val totalPages: Int
)