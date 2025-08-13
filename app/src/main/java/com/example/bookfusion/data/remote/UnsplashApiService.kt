package com.example.bookfusion.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.bookfusion.BuildConfig
import com.example.bookfusion.model.UnsplashSearchResponse


interface UnsplashApiService {
    @GET("search/photos")
    suspend fun searchImages(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("client_id") accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): UnsplashSearchResponse
}