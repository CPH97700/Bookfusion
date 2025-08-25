package com.example.bookfusion.ui.screens.journal.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bookapp.model.BookItem

/**
 * BottomSheet zum Bewerten eines Buches.
 *
 * - Zeigt den Titel des Buches an
 * - Ermöglicht über einen Slider eine Bewertung von 0 bis 5 Sternen (in 0.5-Schritten)
 * - Anzeige der aktuellen Bewertung
 * - Buttons zum Löschen, Schließen oder Speichern der Bewertung
 *
 * @param book Das Buch, das bewertet werden soll
 * @param onClose Aktion beim Schließen ohne Speichern
 * @param onConfirm Aktion beim Speichern der Bewertung (liefert das Rating als Double)
 * @param onDelete Aktion zum Löschen der Bewertung (optional)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookRatingSheet(
    book: BookItem,
    onClose: () -> Unit,
    onConfirm: (rating: Double) -> Unit,
    onDelete: () -> Unit = {}
) {
    ModalBottomSheet(onDismissRequest = onClose) {
        var rating by remember { mutableStateOf(3.0f) }

        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(book.volumeInfo.title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("Bewerte dieses Buch")
            Spacer(Modifier.height(12.dp))

            Slider(
                value = rating,
                onValueChange = { rating = (Math.round(it * 2) / 2f) },
                valueRange = 0f..5f,
                steps = 9
            )

            Text("Aktuelle Bewertung: ${"%.1f".format(rating)} ⭐")

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f)
                ) { Text("Löschen") }

                OutlinedButton(
                    onClick = onClose,
                    modifier = Modifier.weight(1f)
                ) { Text("Schließen") }

                Button(
                    onClick = { onConfirm(rating.toDouble()) },
                    modifier = Modifier.weight(1f)
                ) { Text("Speichern") }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
