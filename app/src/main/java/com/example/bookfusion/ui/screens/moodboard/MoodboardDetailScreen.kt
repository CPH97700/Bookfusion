package com.example.bookfusion.ui.screens.moodboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookfusion.model.UnsplashPhoto
import com.example.bookfusion.ui.screens.moodboard.components.PhotoGrid
import com.example.bookfusion.ui.screens.moodboard.components.UnsplashSearchBar
import com.example.bookfusion.viewmodel.MoodboardViewModel
import com.example.bookfusion.viewmodel.UnsplashViewModel
import kotlinx.coroutines.launch

/**
 * **MoodboardDetailScreen**
 *
 * Zeigt die **Detailansicht eines Moodboards** für ein bestimmtes Buch.
 * Features:
 * - 📸 Anzeige aller bereits gespeicherten Fotos (Moodboard-Einträge)
 * - 🔍 Unsplash-Suche nach neuen Fotos (per Titel voreingestellt)
 * - ➕ Möglichkeit, Fotos ins Moodboard zu übernehmen
 * - 🔙 Navigation zurück über die TopBar
 * - 🍞 Snackbar-Benachrichtigung bei Aktionen
 *
 * @param bookId Die eindeutige ID des Buches
 * @param title Titel des Buches → wird automatisch auch für die Unsplash-Suche genutzt
 * @param onBack Callback, wenn der Nutzer zurück navigieren möchte
 * @param moodboardVM ViewModel für gespeicherte Moodboard-Daten
 * @param unsplashVM ViewModel für Unsplash-Suche und Suchzustände
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodboardDetailScreen(
    bookId: String,
    title: String,
    onBack: () -> Unit,
    moodboardVM: MoodboardViewModel = viewModel(),
    unsplashVM: UnsplashViewModel = viewModel()
) {
    LaunchedEffect(bookId, title) {
        unsplashVM.updateQuery(title)
        unsplashVM.search()
    }

    val entries by moodboardVM.entries.collectAsStateWithLifecycle()
    val saved: List<UnsplashPhoto> = remember(entries, bookId) {
        entries.firstOrNull { it.bookId == bookId }?.photos.orEmpty()
    }

    val ui by unsplashVM.ui.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Gespeichert", style = MaterialTheme.typography.titleMedium)
            if (saved.isEmpty()) {
                Text("Noch keine Fotos gespeichert.")
            } else {
                PhotoGrid(photos = saved, onPick = {
                })
            }

            HorizontalDivider()

            UnsplashSearchBar(
                query = ui.query,
                onQueryChange = unsplashVM::updateQuery,
                onSearch = unsplashVM::search
            )

            when {
                ui.isLoading -> Box(
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                ui.error != null -> Text(
                    "Fehler: ${ui.error}",
                    color = MaterialTheme.colorScheme.error
                )

                else -> {
                    Text("Ergebnisse", style = MaterialTheme.typography.titleMedium)
                    PhotoGrid(photos = ui.photos) { p ->
                        moodboardVM.addPhoto(bookId, p)
                        scope.launch { snackbar.showSnackbar("Zum Moodboard hinzugefügt") }
                    }
                }
            }
        }
    }
}
