package com.example.bookfusion.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bookapp.model.BookItem
import com.example.bookfusion.ui.components.BookCover // ggf. anpassen je nach Dateipfad

@Composable
fun BookShelfRow(
    books: List<BookItem>,
    onBookClick: (BookItem) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(books) { book ->
            BookCover(book = book, onClick = { onBookClick(book) })
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
