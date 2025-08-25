package com.example.bookfusion.ui.screens.journal.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bookapp.model.BookItem

/**
 * Zeigt das Cover eines Buches an.
 *
 * - Wenn das Buch ein Cover-Bild hat, wird es geladen und angezeigt.
 * - Wenn kein Cover vorhanden ist, wird ein Platzhalter-Icon angezeigt.
 * - Das Cover ist anklickbar und ruft dann [onClick] auf.
 *
 * @param book Das Buch, dessen Cover angezeigt werden soll
 * @param onClick Aktion, wenn das Cover angetippt wird
 */
@Composable
fun BookCover(book: BookItem, onClick: () -> Unit) {
    val imageUrl = book.volumeInfo.imageLinks?.thumbnail

    Box(
        modifier = Modifier
            .width(100.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable() { onClick() }
            .shadow(4.dp)
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = book.volumeInfo.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = "Kein Cover",
                    modifier = Modifier.size(40.dp),
                    tint = Color.Gray
                )
            }
        }
    }
}
