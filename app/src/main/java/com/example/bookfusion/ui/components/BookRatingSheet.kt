package com.example.bookfusion.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bookapp.model.BookItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookRatingSheet(
    book: BookItem,
    onClose: () -> Unit,
    onConfirm: (rating: Double) -> Unit,   // <-- neu
    onDelete: () -> Unit = {}              // optional beibehalten
) {
    ModalBottomSheet(onDismissRequest = onClose) {
        var rating by remember { mutableStateOf(3.0f) } // Default 3.0

        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(book.volumeInfo.title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("Bewerte dieses Buch")
            Spacer(Modifier.height(12.dp))

            // 0..5, in 0.5-Schritten (steps = 9, da min/max ausgenommen)
            Slider(
                value = rating,
                onValueChange = { rating = (Math.round(it * 2) / 2f) }, // sauber auf 0.5 runden
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
                    onClick = { onConfirm(rating.toDouble()) },  // <-- gibt Wert zurück
                    modifier = Modifier.weight(1f)
                ) { Text("Speichern") }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
