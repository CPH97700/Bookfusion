package com.example.bookfusion.ui.screens.moodboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookfusion.model.MoodboardEntry
import com.example.bookfusion.ui.screens.moodboard.components.EmptyState
import com.example.bookfusion.ui.screens.moodboard.components.MoodboardLibraryCard
import com.example.bookfusion.viewmodel.MoodboardViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * **MoodBoardScreen**
 *
 * Diese Ansicht zeigt die **Übersicht aller Moodboards** eines Nutzers.
 *
 * Features:
 * - 📚 Anzeige aller Bücher, für die es Moodboard-Einträge gibt
 * - 🖼 Klick auf eine Karte öffnet die Detailansicht des Moodboards
 * - 🗑 Löschen eines Moodboards mit Sicherheitsabfrage (AlertDialog)
 * - ➕ Falls keine Moodboards existieren, wird ein **Empty State** mit Hinweistext angezeigt
 * - 🎨 Hintergrund mit sanftem Farbverlauf (Gradient)
 *
 * @param entriesFlow Ein Flow mit allen gespeicherten Moodboard-Einträgen
 * @param onOpenMoodboard Callback, wenn der Nutzer ein bestimmtes Moodboard öffnen möchte
 * @param modifier Optionaler Modifier (z. B. für Padding)
 * @param moodboardVM ViewModel, das Moodboard-Operationen (z. B. Löschen) ausführt
 * @param onBack Callback für die Zurück-Navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodBoardScreen(
    entriesFlow: StateFlow<List<MoodboardEntry>>,
    onOpenMoodboard: (bookId: String, title: String) -> Unit,
    modifier: Modifier = Modifier,
    moodboardVM: MoodboardViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val entries by entriesFlow.collectAsState()

    val bgTop = Color(0xFFF7ECFF)
    val bgBottom = Color(0xFFEAF2FF)

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var pending by remember { mutableStateOf<MoodboardEntry?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Moodboard") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { inner ->
        Box(
            modifier = modifier
                .padding(inner)
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(bgTop, bgBottom)))
        ) {
            if (entries.isEmpty()) {
                EmptyState(
                    title = "Noch nichts hier …",
                    subtitle = "Füge Bücher aus deinem Journal hinzu, um Moodboards zu bauen.",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(entries, key = { it.bookId }) { e ->
                        MoodboardLibraryCard(
                            title = e.title,
                            coverUrl = e.coverUrl,
                            count = e.photos.size,
                            onOpen = { onOpenMoodboard(e.bookId, e.title) },
                            onDelete = { pending = e }
                        )
                    }
                }
            }
        }
    }

    pending?.let { del ->
        AlertDialog(
            onDismissRequest = { pending = null },
            title = { Text("Buch löschen?") },
            text = { Text("„${del.title}“ und sein Moodboard werden entfernt. Das kann nicht rückgängig gemacht werden.") },
            icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
            confirmButton = {
                TextButton(onClick = {
                    pending = null
                    moodboardVM.deleteEntry(del.bookId)
                    scope.launch { snackbar.showSnackbar("„${del.title}“ gelöscht") }
                }) { Text("Löschen") }
            },
            dismissButton = { TextButton(onClick = { pending = null }) { Text("Abbrechen") } }
        )
    }
}
