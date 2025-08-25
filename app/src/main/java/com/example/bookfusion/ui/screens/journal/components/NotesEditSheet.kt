package com.example.bookfusion.ui.screens.journal.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bookapp.model.BookItem

/**
 * BottomSheet zum Bearbeiten von Notizen zu einem Buch.
 *
 * - Zeigt den Titel des ausgewählten Buches an
 * - Eingabefeld für persönliche Notizen (Gedanken, Zitate, Ideen …)
 * - Zählt die Zeichenanzahl live mit
 * - Buttons zum Abbrechen oder Speichern (nur aktiv, wenn sich der Text geändert hat)
 *
 * @param book Das Buch, für das die Notizen bearbeitet werden
 * @param initialNotes Der aktuelle Notiz-Text (vorhandene Notizen oder leer)
 * @param onClose Aktion beim Abbrechen/Schließen
 * @param onSave Aktion beim Speichern der neuen Notizen (liefert den Text zurück)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesEditSheet(
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
