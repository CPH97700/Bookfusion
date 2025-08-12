package com.example.bookapp.data.repository

import com.example.bookapp.model.BookItem

interface BookRepository {
    suspend fun getRandomBook(): BookItem?
}
