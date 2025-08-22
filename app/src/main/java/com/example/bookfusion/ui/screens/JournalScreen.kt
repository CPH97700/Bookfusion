// app/src/main/java/com/example/bookapp/ui/screens/JournalScreen.kt
package com.example.bookapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.bookapp.model.BookItem
import com.example.bookapp.viewmodel.BookViewModel
import com.example.bookfusion.model.FirestoreBook
import com.example.bookfusion.ui.components.BookRatingSheet
import com.example.bookfusion.ui.components.BookShelfRow
import com.example.bookfusion.ui.components.ManualEntrySheet

@Composable
fun JournalScreen(
    viewModel: BookViewModel = viewModel(),
    onOpenMoodboard: (bookId: String, title: String) -> Unit = { _, _ -> }
) {
    val likedBooks = viewModel.likedBooks
    val readBooks = viewModel.readBooks

    var selectedBook by remember { mutableStateOf<BookItem?>(null) }
    var showManualEntry by remember { mutableStateOf(false) }
    var showRatingSheet by remember { mutableStateOf(false) }
    var showNotesSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFFFE6F0), Color(0xFFD9C6F5))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "📖 Journal",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF2A1A5E)
                )
            }

            Spacer(Modifier.height(16.dp))

            if (likedBooks.isNotEmpty()) {
                Text(
                    "⭐ Favoriten",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF2A1A5E)
                )
                BookShelfRow(
                    books = likedBooks,
                    onBookClick = { selectedBook = it },
                    showLike = false
                )
            }

            if (readBooks.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                Text(
                    "📘 Gelesen & Bewertet",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF2A1A5E)
                )
                BookShelfRow(
                    books = readBooks,
                    onBookClick = { selectedBook = it },
                    showLike = false
                )
            }

            if (likedBooks.isEmpty() && readBooks.isEmpty()) {
                Spacer(Modifier.height(48.dp))
                Text(
                    "Noch keine Bücher im Regal 📚",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        FloatingActionButton(
            onClick = { showManualEntry = true },
            containerColor = Color(0xFF2A1A5E),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .zIndex(10f)
                .padding(16.dp)
                .padding(WindowInsets.navigationBars.asPaddingValues())
                .padding(bottom = 72.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Buch hinzufügen")
        }
    }

    selectedBook?.let { book ->
        val meta = viewModel.getMetaFor(book.id)
        BookDetailBottomSheet(
            book = book,
            meta = meta,
            onClose = { selectedBook = null },
            onEdit = { showRatingSheet = true },
            onMoodboardClick = {
                // Navigation wird von AppNavigation geliefert
                onOpenMoodboard(book.id, book.volumeInfo.title)
            },
            onEditNotes = { showNotesSheet = true }
        )
    }

    if (showRatingSheet && selectedBook != null) {
        BookRatingSheet(
            book = selectedBook!!,
            onClose = { showRatingSheet = false },
            onConfirm = { rating ->
                viewModel.rateBook(selectedBook!!, rating)
                showRatingSheet = false
            },
            onDelete = {
                viewModel.deleteBookFromFirestore(selectedBook!!.id)
                showRatingSheet = false
                selectedBook = null
            }
        )
    }

    if (showNotesSheet && selectedBook != null) {
        val currentNotes = viewModel.getMetaFor(selectedBook!!.id)?.notes.orEmpty()
        NotesEditSheet(
            book = selectedBook!!,
            initialNotes = currentNotes,
            onClose = { showNotesSheet = false },
            onSave = { newNotes ->
                viewModel.updateNotes(selectedBook!!.id, newNotes)
                showNotesSheet = false
            }
        )
    }

    if (showManualEntry) {
        ManualEntrySheet(
            viewModel = viewModel,
            onClose = { showManualEntry = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookDetailBottomSheet(
    book: BookItem,
    meta: FirestoreBook?,
    onClose: () -> Unit,
    onEdit: () -> Unit,
    onMoodboardClick: () -> Unit,
    onEditNotes: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onClose) {
        Column(Modifier.padding(16.dp)) {
            Text("Buch‑Details", style = MaterialTheme.typography.titleLarge, color = Color(0xFF2A1A5E))
            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth()) {
                val cover = book.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")
                AsyncImage(
                    model = cover,
                    contentDescription = book.volumeInfo.title,
                    modifier = Modifier
                        .size(96.dp)
                        .background(Color(0xFFF2F2F2))
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = book.volumeInfo.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    val author = book.volumeInfo.authors?.joinToString(", ") ?: "Unbekannt"
                    Text(text = author, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                    val statusLabel = when (meta?.status) {
                        "READ" -> "Gelesen"
                        "FAVORITE" -> "Favorit"
                        else -> "–"
                    }
                    Spacer(Modifier.height(6.dp))
                    AssistChip(onClick = {}, label = { Text(statusLabel) })
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = meta?.rating?.let { String.format("%.1f ★", it) } ?: "Keine Bewertung",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Notizen",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF2A1A5E),
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onEditNotes) { Text("Bearbeiten") }
            }
            Spacer(Modifier.height(6.dp))
            Surface(
                tonalElevation = 1.dp,
                shape = MaterialTheme.shapes.medium,
                color = Color(0xFFF8F5FF)
            ) {
                Text(
                    meta?.notes ?: "Keine Notizen hinzugefügt.",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onMoodboardClick, modifier = Modifier.weight(1f)) {
                    Text("Zum Moodboard hinzufügen")
                }
                Button(onClick = onEdit, modifier = Modifier.weight(1f)) {
                    Text("Bewerten / Bearbeiten")
                }
            }

            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onClose, modifier = Modifier.align(Alignment.End)) { Text("Schließen") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotesEditSheet(
    book: BookItem,
    initialNotes: String,
    onClose: () -> Unit,
    onSave: (String) -> Unit
) {
    var notes by remember(initialNotes) { mutableStateOf(initialNotes) }
    val canSave = notes != initialNotes

    ModalBottomSheet(onDismissRequest = onClose) {
        Column(Modifier.padding(16.dp)) {
            Text("Notizen bearbeiten", style = MaterialTheme.typography.titleLarge, color = Color(0xFF2A1A5E))
            Spacer(Modifier.height(12.dp))

            Text(
                text = book.volumeInfo.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF2A1A5E)
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp),
                placeholder = { Text("Deine Gedanken, Zitate, Ideen …") },
                supportingText = { Text("${notes.length} Zeichen", style = MaterialTheme.typography.labelSmall) },
                singleLine = false
            )

            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onClose, modifier = Modifier.weight(1f)) { Text("Abbrechen") }
                Button(onClick = { onSave(notes) }, enabled = canSave, modifier = Modifier.weight(1f)) {
                    Text("Speichern")
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
