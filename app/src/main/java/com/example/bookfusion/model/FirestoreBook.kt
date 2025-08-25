package com.example.bookfusion.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Ein Buch, das ich in Firestore speichere.
 *
 * @param id die Buch-ID
 * @param title der Titel des Buches
 * @param author der Autor oder die Autorin
 * @param coverUrl Link zum Coverbild
 * @param status z. B. FAVORITE oder READ
 * @param rating meine Bewertung (kann leer sein)
 * @param updatedAt Zeitstempel der letzten Änderung
 * @param notes eigene Notizen zum Buch
 */
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

/**
 * Ein Eintrag für ein Moodboard in Firestore.
 *
 * Enthält ein Buch mit mehreren Fotos von Unsplash.
 */
data class FirestoreMoodboardEntry(
    val bookId: String = "",
    val title: String = "",
    val coverUrl: String = "",
    val photos: List<FirestoreUnsplashPhoto> = emptyList()
)

/**
 * Ein einzelnes Unsplash-Foto, wie es in Firestore gespeichert wird.
 */
data class FirestoreUnsplashPhoto(
    val id: String = "",
    val smallUrl: String = "",
    val thumbUrl: String = "",
    val alt: String = ""
)

/**
 * Moodboard-Eintrag in der App (nicht in Firestore).
 *
 * Nutzt direkt die [UnsplashPhoto]-Klasse.
 */
data class MoodboardEntry(
    val bookId: String,
    val title: String,
    val coverUrl: String,
    val photos: List<UnsplashPhoto> = emptyList()
)

/**
 * Antwort der Unsplash-Suche.
 *
 * @param total Gesamtanzahl der Treffer
 * @param totalPages wie viele Seiten Ergebnisse es gibt
 * @param results Liste der gefundenen Fotos
 */
@JsonClass(generateAdapter = true)
data class UnsplashSearchResponse(
    val total: Int = 0,
    @Json(name = "total_pages") val totalPages: Int = 0,
    val results: List<UnsplashPhoto> = emptyList()
)

/**
 * Ein Foto von Unsplash.
 *
 * @param id die ID des Fotos
 * @param description optionale Beschreibung
 * @param alt_description Alternativtext
 * @param urls verschiedene Bildgrößen
 */
@JsonClass(generateAdapter = true)
data class UnsplashPhoto(
    val id: String = "",
    val description: String? = null,
    @Json(name = "alt_description") val alt_description: String? = null,
    val urls: UnsplashUrls = UnsplashUrls()
)

/**
 * Verschiedene Größen/Links für ein Unsplash-Foto.
 */
@JsonClass(generateAdapter = true)
data class UnsplashUrls(
    val raw: String = "",
    val full: String = "",
    val regular: String = "",
    val small: String = "",
    val thumb: String = ""
)

/**
 * Wandelt einen Firestore-Moodboard-Eintrag in ein normales Moodboard um.
 */
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

/**
 * Wandelt ein Moodboard in das Firestore-Format um.
 */
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
