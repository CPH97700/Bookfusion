package com.example.bookfusion.ui.screens.journal.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bookapp.model.BookItem
import com.example.bookapp.viewmodel.BookViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Ein BottomSheet, mit dem Nutzer ein Buch manuell ins Journal eintragen können.
 *
 * Ablauf:
 * - Nutzer gibt Titel oder ISBN in ein Suchfeld ein
 * - Ergebnisse von der Google Books API werden angezeigt
 * - Nutzer wählt ein Buch aus
 * - Danach können Bewertung und Notizen hinzugefügt werden
 * - Mit Klick auf den Button wird das Buch in Firestore als gelesen gespeichert
 *
 * @param viewModel Zugriff auf [BookViewModel], um Bücher zu suchen und zu speichern
 * @param onClose Aktion beim Schließen des Sheets
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualEntrySheet(
    viewModel: BookViewModel,
    onClose: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(4.0f) }
    var selected by remember { mutableStateOf<BookItem?>(null) }

    val results by viewModel.searchResults.collectAsState()
    val scope = rememberCoroutineScope()
    var debounce: Job? by remember { mutableStateOf(null) }

    ModalBottomSheet(onDismissRequest = onClose) {
        Column(Modifier.padding(16.dp)) {
            Text("Buch manuell hinzufügen", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    debounce?.cancel()
                    debounce = scope.launch {
                        delay(350)
                        viewModel.searchBooksByQuery(query)
                    }
                },
                label = { Text("ISBN (10/13) oder Titel") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            if (selected == null) {
                if (results.isNotEmpty()) {
                    Text("Treffer", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 320.dp)
                    ) {
                        items(results) { item ->
                            SearchRow(item) { chosen -> selected = chosen }
                        }
                    }
                } else if (query.isNotBlank()) {
                    Text("Keine Treffer gefunden.", color = Color.Gray)
                }
            } else {
                PreviewWithInputs(
                    item = selected!!,
                    rating = rating,
                    onRatingChange = { rating = it },
                    notes = notes,
                    onNotesChange = { notes = it }
                )

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.rateBookWithNotes(
                            book = selected!!,
                            rating = rating.toDouble(),
                            notes = notes.ifBlank { null }
                        )
                        onClose()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Als gelesen speichern") }

                TextButton(
                    onClick = { selected = null },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) { Text("Andere Auswahl") }
            }
        }
    }
}

@Composable
private fun SearchRow(item: BookItem, onPick: (BookItem) -> Unit) {
    val title = item.volumeInfo.title
    val author = item.volumeInfo.authors?.joinToString(", ") ?: "Unbekannt"
    val cover = item.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPick(item) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = cover,
            contentDescription = title,
            modifier = Modifier
                .size(56.dp)
                .background(Color(0xFFF2F2F2))
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(author, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}

@Composable
private fun PreviewWithInputs(
    item: BookItem,
    rating: Float,
    onRatingChange: (Float) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit
) {
    val title = item.volumeInfo.title
    val author = item.volumeInfo.authors?.joinToString(", ") ?: "Unbekannt"
    val cover = item.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")

    Row(Modifier.fillMaxWidth()) {
        AsyncImage(
            model = cover,
            contentDescription = title,
            modifier = Modifier
                .size(96.dp)
                .background(Color(0xFFF2F2F2))
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(author, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }

    Spacer(Modifier.height(12.dp))

    Text("Bewertung: ${"%.1f".format(rating)} ★")
    Slider(
        value = rating,
        onValueChange = onRatingChange,
        valueRange = 0f..5f,
        steps = 8
    )

    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        label = { Text("Notizen (optional)") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 3
    )
}
