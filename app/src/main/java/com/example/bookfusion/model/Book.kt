package com.example.bookapp.model

import com.squareup.moshi.Json

data class BookResponse(
    @Json(name = "items") val items: List<BookItem> = emptyList()
)

data class BookItem(
    @Json(name = "id") val id: String = "",
    @Json(name = "volumeInfo") val volumeInfo: VolumeInfo = VolumeInfo()
) {
    val coverUrl: String?
        get() = volumeInfo.imageLinks?.thumbnail
            ?.replace("http://", "https://")

    val authorString: String
        get() = volumeInfo.authors?.joinToString(", ") ?: "Unbekannter Autor"

    val titleText: String
        get() = volumeInfo.title
}

data class VolumeInfo(
    @Json(name = "title") val title: String = "",
    @Json(name = "authors") val authors: List<String>? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "imageLinks") val imageLinks: ImageLinks? = null
)

data class ImageLinks(
    @Json(name = "thumbnail") val thumbnail: String = ""
)
