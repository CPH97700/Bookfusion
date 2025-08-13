package com.example.bookfusion.model


import com.squareup.moshi.Json

data class UnsplashSearchResponse(
    val total: Int,
    @Json(name = "total_pages") val totalPages: Int,
    val results: List<UnsplashPhoto>
)

data class UnsplashPhoto(
    val id: String,
    val description: String?,
    val alt_description: String?,
    val urls: UnsplashUrls,
    val user: UnsplashUser
)

data class UnsplashUrls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String
)

data class UnsplashUser(
    val id: String,
    val name: String,
    @Json(name = "username") val userName: String
)
