package com.example.bookfusion.ui.screens.moodboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * **MoodboardLibraryCard** – eine Karte für ein Moodboard in der Übersicht.
 *
 * Features:
 * - 📚 Zeigt das Buchcover an (falls keins da ist, wird ein Platzhalter genutzt)
 * - 📝 Unter dem Cover steht der Buchtitel (max. 2 Zeilen)
 * - 📷 Optional ein Chip, der die Anzahl der Fotos im Moodboard zeigt
 * - 🗑️ Lösch-Button oben rechts, um den Eintrag zu entfernen
 *
 * @param title Titel des Moodboard-Buches
 * @param coverUrl URL zum Buchcover (oder leer, dann Platzhalterfarbe)
 * @param count Anzahl der zugeordneten Fotos
 * @param onOpen Aktion, wenn auf die Karte oder den Chip geklickt wird
 * @param onDelete Aktion, wenn der Nutzer das Moodboard löschen möchte
 */
@Composable
fun MoodboardLibraryCard(
    title: String,
    coverUrl: String,
    count: Int,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        onClick = onOpen,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFFFF7FE)),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            Box {
                AsyncImage(
                    model = coverUrl.ifBlank { null },
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFEAE6FF))
                )

                // Sichtbarer Lösch-Button oben rechts
                Surface(
                    color = Color.White.copy(alpha = 0.92f),
                    shape = CircleShape,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(36.dp)
                ) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Löschen",
                            tint = Color(0xFFB00020)
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (count > 0) {
                Spacer(Modifier.height(8.dp))
                AssistChip(onClick = onOpen, label = { Text("$count Foto${if (count == 1) "" else "s"}") })
            }
        }
    }
}
