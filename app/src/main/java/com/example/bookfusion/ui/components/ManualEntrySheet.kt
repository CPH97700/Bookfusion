package com.example.bookfusion.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bookapp.model.BookItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualEntrySheet(onClose: () -> Unit, onSave: (BookItem) -> Unit) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onClose) {
        Column(Modifier.padding(16.dp)) {
            Text("Buch manuell hinzufügen", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titel") })
            OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Autor") })

            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                val newBook = BookItem(
                    id = "manual-${System.currentTimeMillis()}",
                    volumeInfo = com.example.bookapp.model.VolumeInfo(
                        title = title,
                        authors = listOf(author),
                        imageLinks = null
                    )
                )
                onSave(newBook)
            }) {
                Text("Speichern")
            }
        }
    }
}
