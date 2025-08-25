package com.example.bookapp.data.repository

import com.example.bookapp.model.BookItem

/**
 * Repository-Schnittstelle für Bücher.
 *
 * Hier definiere ich, welche Funktionen ein Repository
 * für Bücher mindestens haben muss.
 */
interface BookRepository {
    suspend fun getRandomBook(): BookItem?
}
