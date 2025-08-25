// app/src/main/java/com/example/bookapp/ui/screens/JournalScreen.kt
package com.example.bookapp.ui.screens

import android.util.Log
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
import com.example.bookfusion.ui.screens.journal.components.BookRatingSheet
import com.example.bookfusion.ui.screens.journal.components.BookShelfRow
import com.example.bookfusion.ui.screens.journal.components.ManualEntrySheet
// ⬇️ neu:
import com.example.bookfusion.ui.screens.journal.components.BookDetailBottomSheet
import com.example.bookfusion.ui.screens.journal.components.NotesEditSheet

/**
 * Das Journal zeigt die persönliche Sammlung des Nutzers:
 *
 * - **Favoriten** (vom Nutzer gelikte Bücher)
 * - **Gelesene & Bewertete Bücher** (mit Rating und optionalen Notizen)
 *
 * Zusätzlich:
 * - Bücher können über ein FloatingActionButton manuell hinzugefügt werden
 * - Klick auf ein Buch öffnet ein BottomSheet mit Details und Aktionen:
 *   - Bewerten / Bearbeiten
 *   - Notizen hinzufügen/ändern
 *   - Zum Moodboard hinzufügen
 * - Unterstützt mehrere Sheets: Detail, Bewertung, Notizen, Manuelle Eingabe
 *
 * @param viewModel Das [BookViewModel] für Bücher-Logik
 * @param onOpenMoodboard Callback zum Öffnen eines Moodboards für ein bestimmtes Buch
 */
private const val TAG = "JournalScreen"

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
            .padding(WindowInsets.statusBars.asPaddingValues())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.Start
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
                    onBookClick = {
                        Log.d(TAG, "Liked book clicked: id=${it.id}, title='${it.volumeInfo.title}'")
                        selectedBook = it
                    },
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
                    onBookClick = {
                        Log.d(TAG, "Read book clicked: id=${it.id}, title='${it.volumeInfo.title}'")
                        selectedBook = it
                    },
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
            onClick = {
                Log.d(TAG, "FAB clicked -> open ManualEntrySheet")
                showManualEntry = true
            },
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
            onClose = {
                Log.d(TAG, "BottomSheet close tapped for bookId=${book.id}")
                selectedBook = null
            },
            onEdit = {
                Log.d(TAG, "Open rating sheet for bookId=${book.id}, title='${book.volumeInfo.title}'")
                showRatingSheet = true
            },
            onMoodboardClick = {
                Log.d(TAG, "Open Moodboard for bookId=${book.id}, title='${book.volumeInfo.title}'")
                onOpenMoodboard(book.id, book.volumeInfo.title)
            },
            onEditNotes = {
                Log.d(TAG, "Open notes sheet for bookId=${book.id}")
                showNotesSheet = true
            }
        )
    }

    if (showRatingSheet && selectedBook != null) {
        BookRatingSheet(
            book = selectedBook!!,
            onClose = {
                Log.d(TAG, "RatingSheet close")
                showRatingSheet = false
            },
            onConfirm = { rating ->
                Log.d(TAG, "Rating confirmed for bookId=${selectedBook!!.id}, rating=$rating")
                viewModel.rateBook(selectedBook!!, rating)
                showRatingSheet = false
            },
            onDelete = {
                val id = selectedBook!!.id
                Log.d(TAG, "Delete tapped in RatingSheet for bookId=$id")
                viewModel.deleteBookFromFirestore(id)
                Log.d(TAG, "deleteBookFromFirestore() invoked for bookId=$id")
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
            onClose = {
                Log.d(TAG, "NotesSheet close for bookId=${selectedBook!!.id}")
                showNotesSheet = false
            },
            onSave = { newNotes ->
                Log.d(TAG, "Notes save for bookId=${selectedBook!!.id}, length=${newNotes.length}")
                viewModel.updateNotes(selectedBook!!.id, newNotes)
                showNotesSheet = false
            }
        )
    }

    if (showManualEntry) {
        ManualEntrySheet(
            viewModel = viewModel,
            onClose = {
                Log.d(TAG, "ManualEntrySheet close")
                showManualEntry = false
            }
        )
    }
}
