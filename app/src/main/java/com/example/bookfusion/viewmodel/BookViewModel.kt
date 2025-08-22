package com.example.bookapp.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookapp.model.BookItem
import com.example.bookapp.model.ImageLinks
import com.example.bookapp.model.VolumeInfo
import com.example.bookfusion.data.remote.BookApi
import com.example.bookfusion.model.FirestoreBook
import com.example.bookfusion.repository.BookRepositoryImpl
import com.example.bookfusion.repository.FirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class DataState { LOADING, READY, ERROR }

class BookViewModel : ViewModel() {

    private val repository = BookRepositoryImpl()
    private val firebaseRepository = FirebaseRepository()

    val firestoreBooks: StateFlow<List<FirestoreBook>> = firebaseRepository.userBooks

    fun getMetaFor(bookId: String): FirestoreBook? =
        firestoreBooks.value.firstOrNull { it.id == bookId }

    private val _bookState = MutableStateFlow<BookItem?>(null)
    val bookState: StateFlow<BookItem?> = _bookState

    private val _uiState = MutableStateFlow(DataState.READY)
    val uiState: StateFlow<DataState> = _uiState

    private val _likedBooks = mutableStateListOf<BookItem>()
    val likedBooks: List<BookItem> = _likedBooks

    private val _readBooks = mutableStateListOf<BookItem>()
    val readBooks: List<BookItem> = _readBooks

    private val _searchResults = MutableStateFlow<List<BookItem>>(emptyList())
    val searchResults: StateFlow<List<BookItem>> = _searchResults

    fun searchBooksByQuery(queryRaw: String) {
        Log.d("BookViewModel", "🔍 searchBooksByQuery gestartet mit: $queryRaw")
        viewModelScope.launch(Dispatchers.IO) {
            val q = queryRaw.trim()
            if (q.isBlank()) {
                _searchResults.value = emptyList()
                Log.d("BookViewModel", "⛔ Leere Suche – keine Ergebnisse")
                return@launch
            }
            val looksLikeIsbn = q.replace("-", "").matches(Regex("""^\d{10}(\d{3})?$"""))
            val query = if (looksLikeIsbn) "isbn:${q.replace("-", "")}" else "intitle:$q"

            runCatching {
                BookApi.retrofitService.searchBooks(query = query, maxResults = 8)
            }.onSuccess { resp ->
                _searchResults.value = resp.items ?: emptyList()
                Log.d("BookViewModel", "✅ ${_searchResults.value.size} Ergebnisse gefunden")

            }.onFailure {
                _searchResults.value = emptyList()
                Log.e("BookViewModel", "❌ Suche fehlgeschlagen: ${it.message}")

            }
        }
    }

    suspend fun searchBookByQuery(queryRaw: String): BookItem? {
        Log.d("BookViewModel", "🔍 searchBookByQuery($queryRaw)")
        val q = queryRaw.trim()
        if (q.isEmpty()) return null

        val isIsbn = q.replace("-", "").matches(Regex("""^\d{10}(\d{3})?$"""))
        val query = if (isIsbn) "isbn:${q.replace("-", "")}" else q

        return try {
            val resp = BookApi.retrofitService.searchBooks(query = query, maxResults = 1)
            val item = resp.items?.firstOrNull()
            if (item == null) null else BookItem(
                id = item.id ?: "manual-${System.currentTimeMillis()}",
                volumeInfo = VolumeInfo(
                    title = item.volumeInfo?.title ?: q,
                    authors = item.volumeInfo?.authors,
                    imageLinks = ImageLinks(
                        thumbnail = item.volumeInfo?.imageLinks?.thumbnail ?: ""
                    )
                )
            )
        } catch (e: Exception) {
            Log.e("BookViewModel", "searchBookByQuery error: $e")
            null
        }
    }

    fun addManualRead(queryOrTitle: String, rating: Double, notes: String) {
        Log.d("BookViewModel", "➕ addManualRead: $queryOrTitle, $rating★")
        viewModelScope.launch(Dispatchers.IO) {
            val found = searchBookByQuery(queryOrTitle)

            val book = found ?: BookItem(
                id = "manual-${System.currentTimeMillis()}",
                volumeInfo = VolumeInfo(
                    title = queryOrTitle.ifBlank { "Unbekannter Titel" },
                    authors = null,
                    imageLinks = null
                )
            )

            if (_readBooks.none { it.id == book.id }) {
                _readBooks.add(0, book)
            }
            _likedBooks.removeAll { it.id == book.id }

            val fb = FirestoreBook(
                id = book.id,
                title = book.volumeInfo.title,
                author = book.volumeInfo.authors?.joinToString(", ") ?: "",
                coverUrl = book.volumeInfo.imageLinks?.thumbnail ?: "",
                status = "READ",
                rating = rating,
                notes = notes
            )
            firebaseRepository.saveBook(fb) { ok ->
                Log.d("BookViewModel", if (ok) "✅ Manual READ gespeichert" else "❌ Manual READ fehlgeschlagen")
            }
        }
    }

    fun rateBookWithNotes(book: BookItem, rating: Double, notes: String?) {
        _likedBooks.removeAll { it.id == book.id }
        if (_readBooks.none { it.id == book.id }) _readBooks.add(0, book)
        saveReadToFirestoreWithNotes(book, rating, notes)
    }

    private fun saveReadToFirestoreWithNotes(book: BookItem, rating: Double, notes: String?) {
        val firestoreBook = FirestoreBook(
            id = book.id,
            title = book.volumeInfo.title,
            author = book.volumeInfo.authors?.joinToString(", ") ?: "",
            coverUrl = book.volumeInfo.imageLinks?.thumbnail ?: "",
            status = "READ",
            rating = rating,
            notes = notes
        )
        firebaseRepository.saveBook(firestoreBook) { success ->
            if (!success) Log.e("BookViewModel", "❌ READ+Notes speichern fehlgeschlagen")
        }
    }

    fun likeBook(book: BookItem) {
        if (_likedBooks.none { it.id == book.id }) {
            _likedBooks.add(book)
        }
        saveFavoriteToFirestore(book)
    }

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
            if (!success) Log.e("BookViewModel", "❌ Favorit konnte nicht gespeichert werden")
        }
    }

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
            if (!success) Log.e("BookViewModel", "❌ READ/Rating konnte nicht gespeichert werden")
        }
    }

    fun deleteBookFromFirestore(bookId: String) {
        firebaseRepository.deleteBook(bookId) { success ->
            if (success) {
                _likedBooks.removeAll { it.id == bookId }
                _readBooks.removeAll { it.id == bookId }
            }
        }
    }

    fun loadUserBooks() {
        firebaseRepository.getBooks { books ->
            if (books != null) {
                _likedBooks.clear()
                _readBooks.clear()
                _likedBooks.addAll(books.filter { it.status == "FAVORITE" }.map { it.toBookItem() })
                _readBooks.addAll(books.filter { it.status == "READ" }.map { it.toBookItem() })
            }
        }
    }

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


    fun updateNotes(bookId: String, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1) Falls bereits Metadaten vorhanden sind: nur Notes ersetzen
            val current = getMetaFor(bookId)
            if (current != null) {
                val updated = current.copy(notes = notes)
                firebaseRepository.saveBook(updated) { success ->
                    if (!success) Log.e("BookViewModel", "❌ updateNotes: Speichern fehlgeschlagen")
                }
                return@launch
            }

            val localBook = (_readBooks + _likedBooks).firstOrNull { it.id == bookId }
            if (localBook != null) {
                _likedBooks.removeAll { it.id == bookId }
                if (_readBooks.none { it.id == bookId }) _readBooks.add(0, localBook)

                val fb = FirestoreBook(
                    id = localBook.id,
                    title = localBook.volumeInfo.title,
                    author = localBook.volumeInfo.authors?.joinToString(", ") ?: "",
                    coverUrl = localBook.volumeInfo.imageLinks?.thumbnail ?: "",
                    status = "READ",
                    rating = getMetaFor(bookId)?.rating, // falls doch irgendwo vorhanden
                    notes = notes
                )
                firebaseRepository.saveBook(fb) { success ->
                    if (!success) Log.e("BookViewModel", "❌ updateNotes: Neu anlegen fehlgeschlagen")
                }
                return@launch
            }

            val minimal = FirestoreBook(
                id = bookId,
                title = "",
                author = "",
                coverUrl = "",
                status = "READ",
                rating = null,
                notes = notes
            )
            firebaseRepository.saveBook(minimal) { success ->
                if (!success) Log.e("BookViewModel", "❌ updateNotes: Minimal anlegen fehlgeschlagen")
            }
        }
    }
}
