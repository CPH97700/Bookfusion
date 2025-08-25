package com.example.bookfusion.ui.screens.moodboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * **EmptyState** – ein einfacher Platzhalter-Screen.
 *
 * Features:
 * - 📦 Zeigt einen dekorativen Kasten (Box) als Symbol
 * - 📝 Titel und Untertitel, um den leeren Zustand zu beschreiben
 * - ✨ Gut geeignet, wenn z. B. noch keine Moodboard-Einträge vorhanden sind
 *
 * @param title Kurzer Hinweis-Titel (z. B. „Noch nichts hier“)
 * @param subtitle Erklärung oder Zusatzinfo
 * @param modifier Optionaler Modifier zum Anpassen von Größe, Padding usw.
 */
@Composable
fun EmptyState(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(Color(0xFFEAE6FF), RoundedCornerShape(28.dp))
        )
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
