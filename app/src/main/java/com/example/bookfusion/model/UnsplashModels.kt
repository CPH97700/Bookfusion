package com.example.bookfusion.data.remote.unsplash

import com.squareup.moshi.Json

data class UnsplashSearchResponse(
    @Json(name = "results") val results: List<UnsplashPhoto> = emptyList(),
    @Json(name = "total") val total: Int? = null,
    @Json(name = "total_pages") val totalPages: Int? = null
)

data class UnsplashPhoto(
    @Json(name = "id") val id: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "alt_description") val altDescription: String? = null,
    @Json(name = "urls") val urls: UnsplashUrls
)

data class UnsplashUrls(
    @Json(name = "thumb") val thumb: String? = null,
    @Json(name = "small") val small: String? = null,
    @Json(name = "regular") val regular: String? = null
)