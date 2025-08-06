package com.example.bookapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookapp.model.BookItem
import com.example.bookapp.viewmodel.BookViewModel
import com.example.bookfusion.ui.components.BookRatingSheet
import com.example.bookfusion.ui.components.BookShelfRow
import com.example.bookfusion.ui.components.ManualEntrySheet
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun JournalScreen(viewModel: BookViewModel = viewModel()) {
    val likedBooks = viewModel.likedBooks
    val readBooks = viewModel.readBooks

    var selectedBook by remember { mutableStateOf<BookItem?>(null) }
    var showManualEntry by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFFFE6F0), Color(0xFFD9C6F5))
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 🗂️ Header mit Titel + Add-Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "📖 Journal",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                modifier = Modifier.weight(1f),
                color = Color(0xFF2A1A5E)
            )
            IconButton(onClick = { showManualEntry = true }) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Buch hinzufügen",
                    tint = Color(0xFF2A1A5E)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ⭐ Favoriten-Regal
        if (likedBooks.isNotEmpty()) {
            Text(
                "⭐ Favoriten",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF2A1A5E)
            )
            BookShelfRow(books = likedBooks, onBookClick = { selectedBook = it })
        }

        // 📘 Gelesene Bücher-Regal
        if (readBooks.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "📘 Gelesen & Bewertet",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF2A1A5E)
            )
            BookShelfRow(books = readBooks, onBookClick = { selectedBook = it })
        }

        // 📭 Leerer Zustand
        if (likedBooks.isEmpty() && readBooks.isEmpty()) {
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                "Noch keine Bücher im Regal 📚",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

    // 📖 Bewertungs-Sheet
    selectedBook?.let { book ->
        BookRatingSheet(book = book, onClose = { selectedBook = null })
    }

    // ➕ Manuelles Hinzufügen
    if (showManualEntry) {
        ManualEntrySheet(
            onClose = { showManualEntry = false },
            onSave = { newBook ->
                viewModel.markAsRead(newBook)
                showManualEntry = false
            }
        )
    }
}
