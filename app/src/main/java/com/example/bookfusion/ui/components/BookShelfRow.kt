package com.example.bookfusion.ui.components

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

@Composable
fun BookShelfRow(
    books: List<BookItem>,
    onBookClick: (BookItem) -> Unit,
    showLike: Boolean = false,                      // <-- neu
    onLike: ((BookItem) -> Unit)? = null            // <-- neu
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(books) { book ->
            Box(
                modifier = Modifier.wrapContentSize()
            ) {
                // Dein vorhandenes Cover-Composable
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
