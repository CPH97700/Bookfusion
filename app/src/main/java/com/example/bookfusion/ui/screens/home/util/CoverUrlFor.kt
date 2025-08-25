package com.example.bookfusion.ui.screens.home.util

import com.example.bookapp.model.BookItem

/**
 * Liefert die URL für das Cover-Bild eines Buches.
 *
 * - Wenn das Buch ein Thumbnail in `imageLinks` hat, wird dieses genutzt.
 * - Falls nicht, wird eine Standard-URL von Google Books mit der Buch-ID gebaut.
 *
 * @param book Das Buch, für das das Cover gesucht wird
 * @param preferZoom Zoom-Level für das Fallback-Cover (Standard: 1)
 * @return URL zum Cover oder null, falls nichts verfügbar
 */
fun coverUrlFor(book: BookItem, preferZoom: Int = 1): String? {
    val fromLinks = book.volumeInfo.imageLinks?.thumbnail
        ?.takeIf { it.isNotBlank() }
        ?.let { it.replace("http://", "https://") }
    if (!fromLinks.isNullOrBlank()) return fromLinks

    val id = book.id
    return if (!id.isNullOrBlank()) {
        "https://books.google.com/books/content?id=$id&printsec=frontcover&img=1&zoom=$preferZoom"
    } else null
}
