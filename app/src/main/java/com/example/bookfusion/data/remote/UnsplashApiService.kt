package com.example.bookfusion.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.bookfusion.BuildConfig
import com.example.bookfusion.model.UnsplashSearchResponse

/**
 * Interface für die Unsplash API.
 *
 * Hier lege ich fest, wie ich Bilder über Unsplash suchen kann.
 */
interface UnsplashApiService {
    @GET("search/photos")


    suspend fun searchImages(
        @Query("query") query: String,           //query Suchwort (z. B. Fantasy,Books,...)
        @Query("page") page: Int = 1,            //page welche Seite der Ergebnisse geladen wird (fängt bei 1 an)
        @Query("per_page") perPage: Int = 20,   //wie viele Bilder pro Seite zurückgegeben werden
        @Query("client_id") accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY  //api key wird automatisch mitgeschickt
    ): UnsplashSearchResponse
}