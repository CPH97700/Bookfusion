package com.example.bookapp.model

import com.squareup.moshi.Json

/**
 * Antwort der Google Books API.
 *
 * Enthält eine Liste von [BookItem]s, die die einzelnen Bücher beschreiben.
 */
data class BookResponse(
    @Json(name = "items") val items: List<BookItem> = emptyList()
)

/**
 * Ein einzelnes Buch-Objekt aus der Antwort.
 *
 * @param id Die eindeutige ID des Buches in der Google Books API.
 * @param volumeInfo Alle Infos über das Buch wie Titel, Autor, Cover usw.
 */
data class BookItem(
    @Json(name = "id") val id: String = "",
    @Json(name = "volumeInfo") val volumeInfo: VolumeInfo = VolumeInfo()
) {
}

/**
 * Detaillierte Informationen zu einem Buch.
 *
 * @param title Der Titel des Buches.
 * @param authors Liste der Autor*innen (kann auch leer sein).
 * @param description Eine kurze Beschreibung oder Zusammenfassung.
 * @param imageLinks Verweise auf Cover-Bilder des Buches.
 */
data class VolumeInfo(
    @Json(name = "title") val title: String = "",
    @Json(name = "authors") val authors: List<String>? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "imageLinks") val imageLinks: ImageLinks? = null
)

/**
 * Hält die Links zu den Buchcovern.
 *
 * @param thumbnail Die URL für ein kleines Cover-Bild.
 */
data class ImageLinks(
    @Json(name = "thumbnail") val thumbnail: String = ""
)
