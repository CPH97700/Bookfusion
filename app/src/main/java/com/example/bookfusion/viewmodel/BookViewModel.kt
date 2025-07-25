package com.example.bookapp.viewmodel

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

    private val repository = BookRepositoryImpl()

    private val _bookState = MutableStateFlow<BookItem?>(null)
    val bookState: StateFlow<BookItem?> = _bookState

    private val _uiState = MutableStateFlow(DataState.READY)
    val uiState: StateFlow<DataState> = _uiState

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
