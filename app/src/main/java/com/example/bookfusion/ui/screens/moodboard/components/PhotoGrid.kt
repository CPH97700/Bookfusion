package com.example.bookfusion.ui.screens.moodboard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import com.example.bookfusion.model.UnsplashPhoto

/**
 * **PhotoGrid** – zeigt eine Gitteransicht mit Unsplash-Fotos.
 *
 * Features:
 * - 📷 Fotos aus der Unsplash-Suche werden als gleichmäßige Quadrate angezeigt
 * - 🖱️ Ein Klick auf ein Foto ruft `onPick` mit dem gewählten Bild auf
 * - 🔑 Items sind mit der Unsplash-Foto-ID als Schlüssel versehen (stabile Liste)
 *
 * @param photos Liste der Unsplash-Fotos, die angezeigt werden sollen
 * @param onPick Callback, das beim Anklicken eines Fotos aufgerufen wird
 */
@Composable
fun PhotoGrid(
    photos: List<UnsplashPhoto>,
    onPick: (UnsplashPhoto) -> Unit
) {
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        state = gridState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(photos, key = { it.id }) { p ->
            AsyncImage(
                model = p.urls.small,
                contentDescription = p.alt_description ?: p.description ?: "Foto",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clickable { onPick(p) }
            )
        }
    }
}
