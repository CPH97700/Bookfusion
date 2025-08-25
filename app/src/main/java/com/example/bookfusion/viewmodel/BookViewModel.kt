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

/** Enum für UI-State (Ladezustand, Fehler, bereit) */
enum class DataState { LOADING, READY, ERROR }

/**
 * **BookViewModel**
 *
 * Zentrale **State- und Logik-Schicht** für Bücher:
 * Steuert UI-State, lädt Daten von **Google Books API**,
 * synchronisiert mit **Firebase Firestore** und verwaltet lokale Pools.
 *
 * ---
 * 🔑 **Features:**
 * - 📚 **Random Book Discovery**: holt Bücher aus Google Books, filtert Self-Help/Biographien raus
 * - ❤️ **Like/Favorite**: speichert als Favorit in Firestore
 * - ⭐ **Rating/Read**: bewertet Bücher, verschiebt in "Read"-Liste, speichert mit optionalen Notizen
 * - ✍️ **Manuelle Eingabe**: ISBN/Titel-Suche & eigenes Hinzufügen
 * - 🔄 **StateFlows**: steuern UI-Reaktivität (`bookState`, `uiState`, `firestoreBooks`, `searchResults`)
 *
 * ---
 * **Architektur-Flow:**
 *
 * UI → **BookViewModel** → (BookRepositoryImpl = Google Books API)
 *                       → (FirebaseRepository = Firestore Sync)
 *
 */
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

    // --------------------- Pool + Genre-Filter ---------------------

    private val preloadPool = ArrayDeque<BookItem>()

    private val subjectQueries = listOf(
        "subject:Fantasy",
        "subject:Romance",
        "subject:Young Adult",
        "subject:Young Adult Fiction",
        "subject:Paranormal",
        "subject:Fantasy Romance"
    )

    private val keywordQueries = listOf(
        "romantasy",
        "\"dark romance\"",
        "\"fae romance\"",
        "\"enemies to lovers\"",
        "\"slow burn\" romance",
        "ya fantasy"
    )

    private val blockedTitleKeywords = listOf(
        "self-help", "self help",
        "biography", "autobiography", "memoir"
    )

    private val seenIds = mutableSetOf<String>()

    private fun isBlockedByTitle(title: String?): Boolean {
        if (title.isNullOrBlank()) return false
        val t = title.lowercase()
        return blockedTitleKeywords.any { t.contains(it) }
    }

    private suspend fun fetchBatch(targetCount: Int = 24): List<BookItem> {
        val queries = (subjectQueries + keywordQueries).shuffled()
        val out = LinkedHashMap<String, BookItem>()

        for (q in queries) {
            if (out.size >= targetCount) break
            try {
                val resp = BookApi.retrofitService.searchBooks(query = q, maxResults = 40)
                val items = resp.items.orEmpty()
                for (it in items) {
                    val id = it.id ?: continue
                    if (id in seenIds || id in out) continue
                    val title = it.volumeInfo?.title
                    if (isBlockedByTitle(title)) continue
                    out[id] = it
                    if (out.size >= targetCount) break
                }
            } catch (e: Exception) {
                Log.e("BookViewModel", "fetchBatch('$q') failed: ${e.message}")
            }
        }
        return out.values.toList()
    }

    private suspend fun ensurePool(minSize: Int = 6) {
        if (preloadPool.size >= minSize) return
        val batch = fetchBatch(targetCount = 24)
        batch.forEach { item ->
            val id = item.id ?: return@forEach
            if (id !in seenIds) {
                preloadPool.addLast(item)
                seenIds.add(id)
            }
        }
    }

    // ---------------------------------------------------------------------------

    fun searchBooksByQuery(queryRaw: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val q = queryRaw.trim()
            if (q.isBlank()) {
                _searchResults.value = emptyList()
                return@launch
            }
            val looksLikeIsbn = q.replace("-", "").matches(Regex("""^\d{10}(\d{3})?$"""))
            val query = if (looksLikeIsbn) "isbn:${q.replace("-", "")}" else "intitle:$q"

            runCatching {
                BookApi.retrofitService.searchBooks(query = query, maxResults = 8)
            }.onSuccess { resp ->
                _searchResults.value = resp.items ?: emptyList()
            }.onFailure {
                _searchResults.value = emptyList()
            }
        }
    }

    suspend fun searchBookByQuery(queryRaw: String): BookItem? {
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
        } catch (_: Exception) {
            null
        }
    }

    fun addManualRead(queryOrTitle: String, rating: Double, notes: String) {
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

            if (_readBooks.none { it.id == book.id }) _readBooks.add(0, book)
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
            firebaseRepository.saveBook(fb) { /* ignore */ }
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
        firebaseRepository.saveBook(firestoreBook) { /* ignore */ }
    }

    fun likeBook(book: BookItem) {
        if (_likedBooks.none { it.id == book.id }) _likedBooks.add(book)
        saveFavoriteToFirestore(book)
    }

    fun rateBook(book: BookItem, rating: Double) {
        _likedBooks.removeAll { it.id == book.id }
        if (_readBooks.none { it.id == book.id }) _readBooks.add(book)
        saveReadToFirestore(book, rating)
    }

    fun markAsRead(book: BookItem) {
        if (_readBooks.none { it.id == book.id }) _readBooks.add(book)
    }

    /**
     * Genres: Fantasy/Romance/YA (inkl. Romantasy/Dark Romance),
     * Self-Help & Biographien werden weggefiltert.
     * Dislike speichert NICHT – die UI ruft nur erneut `loadRandomBook()` auf.
     */
    fun loadRandomBook() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = DataState.LOADING

            try {
                ensurePool(minSize = 6)

                val next = preloadPool.removeFirstOrNull()
                if (next != null) {
                    _bookState.value = next
                    _uiState.value = DataState.READY
                    return@launch
                }

                // Fallback auf bestehendes Repo (selten)
                val fallback = repository.getRandomBook()
                if (fallback != null && !isBlockedByTitle(fallback.volumeInfo?.title)) {
                    val id = fallback.id ?: "fallback-${System.currentTimeMillis()}"
                    if (id !in seenIds) seenIds.add(id)
                    _bookState.value = fallback
                    _uiState.value = DataState.READY
                } else {
                    _uiState.value = DataState.ERROR
                }
            } catch (e: Exception) {
                Log.e("BookViewModel", "loadRandomBook failed: ${e.message}")
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
        firebaseRepository.saveBook(firestoreBook) { /* ignore */ }
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
        firebaseRepository.saveBook(firestoreBook) { /* ignore */ }
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
            val current = getMetaFor(bookId)
            if (current != null) {
                val updated = current.copy(notes = notes)
                firebaseRepository.saveBook(updated) { /* ignore */ }
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
                    rating = getMetaFor(bookId)?.rating,
                    notes = notes
                )
                firebaseRepository.saveBook(fb) { /* ignore */ }
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
            firebaseRepository.saveBook(minimal) { /* ignore */ }
        }
    }
}
