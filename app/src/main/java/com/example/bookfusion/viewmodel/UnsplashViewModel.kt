package com.example.bookfusion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookfusion.model.UnsplashPhoto
import com.example.bookfusion.repository.UnsplashRepository

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
/**
 * Repräsentiert den aktuellen Zustand der Unsplash-Suche im UI.
 * - [isLoading]: zeigt an, ob gerade gesucht wird.
 * - [error]: Fehlermeldung, falls die Suche fehlschlägt.
 * - [photos]: Liste der gefundenen Fotos.
 * - [query]: Der aktuelle Suchbegriff.
 */
data class UnsplashUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val photos: List<UnsplashPhoto> = emptyList(),
    val query: String = ""
)

class UnsplashViewModel(
    private val repo: UnsplashRepository = UnsplashRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(UnsplashUiState())
    val ui: StateFlow<UnsplashUiState> = _ui

    private var currentJob: Job? = null

    fun updateQuery(newQuery: String) {
        _ui.value = _ui.value.copy(query = newQuery)
    }

    fun search() {
        val q = _ui.value.query
        if (q.isBlank()) return

        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true, error = null)
            try {
                val res = repo.search(q)
                _ui.value = _ui.value.copy(
                    isLoading = false,
                    photos = res.results
                )
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unbekannter Fehler"
                )
            }
        }
    }
}
