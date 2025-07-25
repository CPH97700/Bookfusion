package com.example.bookapp.data.remote

import com.example.bookapp.model.BookResponse
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.example.bookfusion.BuildConfig

const val BASE_URL = "https://www.googleapis.com/books/v1/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

interface BookApiService {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("langRestrict") lang: String = "de|en",
        @Query("printType") printType: String = "books",
        @Query("maxResults") maxResults: Int = 20,
        @Query("key") apiKey: String = BuildConfig.GOOGLE_BOOKS_API_KEY
    ): BookResponse
}

object BookApi {
    val retrofitService: BookApiService by lazy {
        retrofit.create(BookApiService::class.java)
    }
}
