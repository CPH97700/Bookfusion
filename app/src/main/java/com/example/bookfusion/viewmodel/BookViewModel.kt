package com.example.bookapp.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookapp.model.BookItem
import com.example.bookfusion.model.FirestoreBook
import com.example.bookfusion.repository.BookRepositoryImpl
import com.example.bookfusion.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.bookapp.model.VolumeInfo
import com.example.bookapp.model.ImageLinks


enum class DataState {
    LOADING, READY, ERROR
}

class BookViewModel : ViewModel() {
    init {
        Log.d("BookViewModel", "📘 ViewModel erstellt")
    }

    private val repository = BookRepositoryImpl()
    private val firebaseRepository = FirebaseRepository()

    private val _bookState = MutableStateFlow<BookItem?>(null)
    val bookState: StateFlow<BookItem?> = _bookState

    private val _uiState = MutableStateFlow(DataState.READY)
    val uiState: StateFlow<DataState> = _uiState

    private val _likedBooks = mutableStateListOf<BookItem>()
    val likedBooks: List<BookItem> = _likedBooks

    private val _readBooks = mutableStateListOf<BookItem>()
    val readBooks: List<BookItem> = _readBooks

    /** ❤️ Nur in Favoriten (nicht mehr automatisch als gelesen markieren) */
    fun likeBook(book: BookItem) {
        Log.d("BookViewModel", "❤️ Buch geliked: ${book.volumeInfo.title}")
        if (_likedBooks.none { it.id == book.id }) {
            _likedBooks.add(book)
        }
        saveFavoriteToFirestore(book)
    }

    /** ⭐ Beim Bewerten: aus Favoriten entfernen, nach Gelesen & Bewertet verschieben */
    fun rateBook(book: BookItem, rating: Double) {
        _likedBooks.removeAll { it.id == book.id }
        if (_readBooks.none { it.id == book.id }) {
            _readBooks.add(book)
        }
        saveReadToFirestore(book, rating)
    }

    fun markAsRead(book: BookItem) {
        if (_readBooks.none { it.id == book.id }) {
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

    /** 🔹 Favorit in Firestore anlegen */
    private fun saveFavoriteToFirestore(book: BookItem) {
        val firestoreBook = FirestoreBook(
            id = book.id,
            title = book.volumeInfo.title,
            author = book.volumeInfo.authors?.joinToString(", ") ?: "",
            coverUrl = book.volumeInfo.imageLinks?.thumbnail ?: "",
            status = "FAVORITE",
            rating = null
        )
        firebaseRepository.saveBook(firestoreBook) { success ->
            if (success) {
                Log.d("BookViewModel", "✅ Als FAVORITE gespeichert: ${book.volumeInfo.title}")
            } else {
                Log.e("BookViewModel", "❌ Favorit konnte nicht gespeichert werden")
            }
        }
    }

    /** 🔹 Gelesen & bewertet in Firestore speichern */
    private fun saveReadToFirestore(book: BookItem, rating: Double) {
        val firestoreBook = FirestoreBook(
            id = book.id,
            title = book.volumeInfo.title,
            author = book.volumeInfo.authors?.joinToString(", ") ?: "",
            coverUrl = book.volumeInfo.imageLinks?.thumbnail ?: "",
            status = "READ",
            rating = rating
        )
        firebaseRepository.saveBook(firestoreBook) { success ->
            if (success) {
                Log.d(
                    "BookViewModel",
                    "✅ Als READ mit Rating $rating gespeichert: ${book.volumeInfo.title}"
                )
            } else {
                Log.e("BookViewModel", "❌ READ/Rating konnte nicht gespeichert werden")
            }
        }
    }

    fun deleteBookFromFirestore(bookId: String) {
        firebaseRepository.deleteBook(bookId) { success ->
            if (success) {
                // >>> UI sofort aktualisieren
                _likedBooks.removeAll { it.id == bookId }
                _readBooks.removeAll { it.id == bookId }

                // (optional) danach noch einmal vom Server ziehen
                // loadUserBooks()

                Log.d("BookViewModel", "🗑 Buch gelöscht: $bookId")
            } else {
                Log.e("BookViewModel", "❌ Buch konnte nicht gelöscht werden")
            }
        }
    }


    /** 🔄 Lädt alle Bücher für den eingeloggten Nutzer aus Firestore */
    fun loadUserBooks() {
        firebaseRepository.getBooks { books ->
            if (books != null) {
                _likedBooks.clear()
                _readBooks.clear()

                _likedBooks.addAll(
                    books.filter { it.status == "FAVORITE" }
                        .map { it.toBookItem() }
                )
                _readBooks.addAll(
                    books.filter { it.status == "READ" }
                        .map { it.toBookItem() }
                )

                Log.d(
                    "BookViewModel",
                    "📚 Bücher geladen → ${_likedBooks.size} Favoriten, ${_readBooks.size} gelesen"
                )
            } else {
                Log.w("BookViewModel", "⚠️ Keine Bücher gefunden oder Fehler beim Laden")
            }
        }
    }

    /** 🔄 Konvertiert FirestoreBook zurück zu BookItem */
    private fun FirestoreBook.toBookItem(): BookItem {
        return BookItem(
            id = this.id,
            volumeInfo = VolumeInfo(
                title = this.title,
                authors = if (this.author.isNotBlank())
                    this.author.split(",").map { it.trim() }
                else null,
                imageLinks = ImageLinks(thumbnail = this.coverUrl)
            )
        )
    }
}
