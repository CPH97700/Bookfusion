package com.example.bookfusion.data.remote

import com.example.bookfusion.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val UNSPLASH_BASE_URL = "https://api.unsplash.com/"

private val authInterceptor = Interceptor { chain ->
    val req = chain.request().newBuilder()
        .addHeader("Authorization", "Client-ID ${BuildConfig.UNSPLASH_ACCESS_KEY}")
        .build()
    chain.proceed(req)
}

private val logging = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BASIC
}

private val okHttp = OkHttpClient.Builder()
    .addInterceptor(authInterceptor)
    .addInterceptor(logging)
    .build()

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(UNSPLASH_BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(okHttp)
    .build()

object UnsplashApi {
    val service: UnsplashApiService by lazy { retrofit.create(UnsplashApiService::class.java) }
}
