package com.example.bookfusion.ui.screens.journal.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bookapp.model.BookItem
import com.example.bookfusion.model.FirestoreBook

/**
 * Zeigt ein BottomSheet mit den Details zu einem Buch.
 *
 * - Cover, Titel und Autor werden angezeigt
 * - Status (Favorit oder Gelesen) wird als Chip dargestellt
 * - Bewertung (Sterne) wird angezeigt
 * - Eigene Notizen sind sichtbar und können bearbeitet werden
 * - Buttons für "Zum Moodboard hinzufügen" und "Bewerten/Bearbeiten"
 *
 * @param book Das Google-Books-Objekt mit Titel, Cover etc.
 * @param meta Zusätzliche Firestore-Daten (Status, Bewertung, Notizen)
 * @param onClose Aktion beim Schließen
 * @param onEdit Aktion beim Bearbeiten/Bewerten
 * @param onMoodboardClick Aktion, um das Buch ins Moodboard aufzunehmen
 * @param onEditNotes Aktion zum Bearbeiten der Notizen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailBottomSheet(
    book: BookItem,
    meta: FirestoreBook?,
    onClose: () -> Unit,
    onEdit: () -> Unit,
    onMoodboardClick: () -> Unit,
    onEditNotes: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onClose) {
        Column(Modifier.padding(16.dp)) {
            Text("Buch-Details", style = MaterialTheme.typography.titleLarge, color = Color(0xFF2A1A5E))
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
