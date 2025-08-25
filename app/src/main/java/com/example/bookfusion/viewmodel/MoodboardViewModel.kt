package com.example.bookfusion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookfusion.model.*
import com.example.bookfusion.repository.MoodboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * **MoodboardViewModel**
 *
 * Steuert die Moodboard-Daten im UI:
 * - Hört auf Änderungen im Repository (live Updates aus Firestore).
 * - Kann Bücher hinzufügen, wenn noch kein Moodboard dafür existiert.
 * - Kann Fotos (Unsplash) zu einem Moodboard hinzufügen oder entfernen.
 * - Kann ganze Moodboards löschen.
 *
 * Nutzt [MoodboardRepository] für alle Datenoperationen.
 */
class MoodboardViewModel(
    private val repo: MoodboardRepository = MoodboardRepository()
) : ViewModel() {

    private val _entries = MutableStateFlow<List<MoodboardEntry>>(emptyList())
    val entries: StateFlow<List<MoodboardEntry>> = _entries

    init {
        viewModelScope.launch {
            repo.listenMoodboards().collectLatest { serverUiEntries ->
                _entries.value = serverUiEntries
            }
        }
    }

    fun addBookIfMissing(bookId: String, title: String, coverUrl: String) {
        viewModelScope.launch { repo.ensureEntry(bookId, title, coverUrl) }
    }

    fun addPhoto(bookId: String, photo: UnsplashPhoto) {
        viewModelScope.launch {
            repo.addPhoto(
                bookId = bookId,
                photo = FirestoreUnsplashPhoto(
                    id = photo.id,
                    smallUrl = photo.urls.small,
                    thumbUrl = photo.urls.thumb,
                    alt = photo.alt_description ?: photo.description.orEmpty()
                )
            )
            _entries.value = _entries.value.map { e ->
                if (e.bookId == bookId && e.photos.none { it.id == photo.id }) {
                    e.copy(photos = e.photos + photo)
                } else e
            }
        }
    }

    fun removePhoto(bookId: String, photoId: String) {
        viewModelScope.launch {
            repo.removePhoto(bookId, photoId)
            _entries.value = _entries.value.map { e ->
                if (e.bookId == bookId) e.copy(photos = e.photos.filterNot { it.id == photoId }) else e
            }
        }
    }

    fun deleteEntry(bookId: String) {
        viewModelScope.launch {
            repo.deleteEntry(bookId)
            _entries.value = _entries.value.filterNot { it.bookId == bookId }
        }
    }
}
