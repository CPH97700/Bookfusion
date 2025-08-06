package com.example.bookapp.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookapp.model.BookItem
import com.example.bookfusion.data.repository.BookRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class DataState {
    LOADING, READY, ERROR
}

class BookViewModel : ViewModel() {
    init {
        Log.d("BookViewModel", "📘 ViewModel erstellt")
    }

    private val repository = BookRepositoryImpl()

    private val _bookState = MutableStateFlow<BookItem?>(null)
    val bookState: StateFlow<BookItem?> = _bookState

    private val _uiState = MutableStateFlow(DataState.READY)
    val uiState: StateFlow<DataState> = _uiState

    private val _likedBooks = mutableStateListOf<BookItem>()
    val likedBooks: List<BookItem> = _likedBooks

    private val _readBooks = mutableStateListOf<BookItem>()
    val readBooks: List<BookItem> = _readBooks

    fun likeBook(book: BookItem) {
        Log.d("BookViewModel", "❤️ Buch geliked: ${book.volumeInfo.title}")
        if (!_likedBooks.any { it.id == book.id }) {
            _likedBooks.add(book)
        }
        markAsRead(book)
    }

    fun markAsRead(book: BookItem) {
        if (!_readBooks.any { it.id == book.id }) {
            _readBooks.add(book)
        }
    }

    fun loadRandomBook() {
        viewModelScope.launch {
            _uiState.value = DataState.LOADING
            val book = repository.getRandomBook()
            if (book != null) {
                _bookState.value = book
                _uiState.value = DataState.READY
            } else {
                _uiState.value = DataState.ERROR
            }
        }
    }
}
