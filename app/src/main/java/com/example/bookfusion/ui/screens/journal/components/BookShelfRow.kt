package com.example.bookfusion.ui.screens.journal.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bookapp.model.BookItem

/**
 * Zeigt eine Reihe von Büchern in einem horizontal scrollbaren Regal.
 *
 * - Nutzt [LazyRow], um Buchcover nebeneinander darzustellen
 * - Optional kann ein Herz-Icon (❤️) angezeigt werden, um ein Buch zu liken
 * - Unter der Reihe wird ein farbiger Divider (Trennlinie) dargestellt
 *
 * @param books Liste der anzuzeigenden Bücher
 * @param onBookClick Aktion, die ausgeführt wird, wenn ein Buch angeklickt wird
 * @param showLike Ob das Herz-Icon angezeigt werden soll (Standard = false)
 * @param onLike Aktion beim Klicken auf das Herz-Icon (nur relevant, wenn [showLike] = true)
 */
@Composable
fun BookShelfRow(
    books: List<BookItem>,
    onBookClick: (BookItem) -> Unit,
    showLike: Boolean = false,
    onLike: ((BookItem) -> Unit)? = null
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(books) { book ->
            Box(
                modifier = Modifier.wrapContentSize()
            ) {
                BookCover(book = book, onClick = { onBookClick(book) })

                if (showLike && onLike != null) {
                    IconButton(
                        onClick = { onLike(book) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Als Favorit speichern",
                        )
                    }
                }
            }
        }
    }

    Divider(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
        thickness = 2.dp,
        color = Color(0xFF9C7DCF)
    )
}
