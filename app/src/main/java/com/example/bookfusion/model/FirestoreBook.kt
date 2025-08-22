package com.example.bookfusion.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


data class FirestoreBook(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val coverUrl: String = "",
    val status: String = "FAVORITE",
    val rating: Double? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val notes: String? = null
)

data class FirestoreMoodboardEntry(
    val bookId: String = "",
    val title: String = "",
    val coverUrl: String = "",
    val photos: List<FirestoreUnsplashPhoto> = emptyList()
)

data class FirestoreUnsplashPhoto(
    val id: String = "",
    val smallUrl: String = "",
    val thumbUrl: String = "",
    val alt: String = ""
)


data class MoodboardEntry(
    val bookId: String,
    val title: String,
    val coverUrl: String,
    val photos: List<UnsplashPhoto> = emptyList()
)


@JsonClass(generateAdapter = true)
data class UnsplashSearchResponse(
    val total: Int = 0,
    @Json(name = "total_pages") val totalPages: Int = 0,
    val results: List<UnsplashPhoto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class UnsplashPhoto(
    val id: String = "",
    val description: String? = null,
    @Json(name = "alt_description") val alt_description: String? = null,
    val urls: UnsplashUrls = UnsplashUrls()
)

@JsonClass(generateAdapter = true)
data class UnsplashUrls(
    val raw: String = "",
    val full: String = "",
    val regular: String = "",
    val small: String = "",
    val thumb: String = ""
)


fun FirestoreMoodboardEntry.toMoodboardEntry(): MoodboardEntry =
    MoodboardEntry(
        bookId = bookId,
        title = title,
        coverUrl = coverUrl,
        photos = photos.map {
            UnsplashPhoto(
                id = it.id,
                urls = UnsplashUrls(
                    raw = it.smallUrl,
                    full = it.smallUrl,
                    regular = it.smallUrl,
                    small = it.smallUrl,
                    thumb = it.thumbUrl
                ),
                alt_description = it.alt
            )
        }
    )

fun MoodboardEntry.toFirestore(): FirestoreMoodboardEntry =
    FirestoreMoodboardEntry(
        bookId = bookId,
        title = title,
        coverUrl = coverUrl,
        photos = photos.map {
            FirestoreUnsplashPhoto(
                id = it.id,
                smallUrl = it.urls.small,
                thumbUrl = it.urls.thumb,
                alt = it.alt_description.orEmpty()
            )
        }
    )
