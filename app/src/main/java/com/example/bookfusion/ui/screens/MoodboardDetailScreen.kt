package com.example.bookfusion.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bookfusion.model.UnsplashPhoto
import com.example.bookfusion.viewmodel.MoodboardViewModel
import com.example.bookfusion.viewmodel.UnsplashViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun  MoodboardDetailScreen(
    bookId: String,
    title: String,
    onBack: () -> Unit,
    moodboardVM: MoodboardViewModel = viewModel(),
    unsplashVM: UnsplashViewModel = viewModel()
) {
    LaunchedEffect(bookId, title) {
        unsplashVM.updateQuery(title)
        unsplashVM.search()
    }

    val entries by moodboardVM.entries.collectAsStateWithLifecycle()
    val saved: List<UnsplashPhoto> = remember(entries, bookId) {
        entries.firstOrNull { it.bookId == bookId }?.photos.orEmpty()
    }

    val ui by unsplashVM.ui.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Gespeichert", style = MaterialTheme.typography.titleMedium)
            if (saved.isEmpty()) {
                Text("Noch keine Fotos gespeichert.")
            } else {
                PhotoGrid(photos = saved, onPick = { /* ggf. Vollbild/Remove */ })
            }

            HorizontalDivider()

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = ui.query,
                    onValueChange = unsplashVM::updateQuery,
                    modifier = Modifier.weight(1f),
                    label = { Text("Unsplash-Suche") },
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = unsplashVM::search) { Text("Suchen") }
            }

            when {
                ui.isLoading -> Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                ui.error != null -> Text("Fehler: ${ui.error}", color = MaterialTheme.colorScheme.error)
                else -> {
                    Text("Ergebnisse", style = MaterialTheme.typography.titleMedium)
                    PhotoGrid(photos = ui.photos) { p ->
                        moodboardVM.addPhoto(bookId, p)
                        scope.launch { snackbar.showSnackbar("Zum Moodboard hinzugefügt") }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoGrid(photos: List<UnsplashPhoto>, onPick: (UnsplashPhoto) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
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
