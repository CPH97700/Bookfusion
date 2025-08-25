package com.example.bookfusion.ui.screens.moodboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * **UnsplashSearchBar** – eine kleine Suchleiste für Unsplash-Fotos.
 *
 * Features:
 * - 🔎 Eingabefeld für den Suchtext
 * - ⌨️ Änderungen im Feld werden per `onQueryChange` zurückgegeben
 * - 📤 Mit Klick auf den Button wird `onSearch` ausgelöst
 *
 * @param query Aktueller Text im Suchfeld
 * @param onQueryChange Callback, wenn der Nutzer Text eingibt
 * @param onSearch Callback, wenn der Nutzer auf „Suchen“ klickt
 */
@Composable
fun UnsplashSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            label = { Text("Unsplash-Suche") },
            singleLine = true
        )
        Spacer(Modifier.width(8.dp))
        Button(onClick = onSearch) { Text("Suchen") }
    }
}
