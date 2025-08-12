package com.example.bookfusion.repository

import android.util.Log
import com.example.bookapp.data.remote.BookApi
import com.example.bookapp.data.repository.BookRepository
import com.example.bookapp.model.BookItem

class BookRepositoryImpl : BookRepository {

    private val api = BookApi.retrofitService

    override suspend fun getRandomBook(): BookItem? {
        return try {
            val query = getRandomQuery()
            Log.d("API", "🔍 Anfrage an GoogleBooks API mit Query: $query")

            val response = api.searchBooks(query)
            val results = response.items ?: emptyList()

            Log.d("API", "📚 Gefundene Ergebnisse: ${results.size}")
            results.randomOrNull()
        } catch (e: Exception) {
            Log.e("API", "❌ Fehler beim Abrufen von Büchern", e)
            null
        }
    }

    private fun getRandomQuery(): String {
        val genres = listOf("young adult fantasy", "dark romance")
        return genres.random()
    }
}
