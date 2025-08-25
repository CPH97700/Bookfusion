package com.example.bookfusion.repository

import com.example.bookfusion.data.remote.UnsplashApi
import com.example.bookfusion.data.remote.UnsplashApiService
import com.example.bookfusion.model.UnsplashSearchResponse

/**
 * Repository für die Unsplash API.
 *
 * Hierüber hole ich Bilder von Unsplash,
 * z. B. um ein Moodboard zu füllen.
 */
class UnsplashRepository(
    private val api: UnsplashApiService = UnsplashApi.service
) {
    suspend fun search(query: String, page: Int = 1, perPage: Int = 20): UnsplashSearchResponse {
        return api.searchImages(query = query.trim(), page = page, perPage = perPage)
    }
}