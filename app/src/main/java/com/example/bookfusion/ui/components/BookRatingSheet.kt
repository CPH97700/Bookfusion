package com.example.bookfusion.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bookapp.model.BookItem
import javax.annotation.WillClose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookRatingSheet(book: BookItem, onClose: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onClose) {
        Column(Modifier.padding(16.dp)) {
            Text(book.volumeInfo.title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("Hier könnte deine Bewertung stehen…")
            Spacer(Modifier.height(16.dp))
            Button(onClick = onClose) {
                Text("Schließen")
            }
        }
    }
}
