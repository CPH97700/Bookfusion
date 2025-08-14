package com.example.bookfusion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.bookfusion.model.UnsplashPhoto
import com.example.bookfusion.viewmodel.UnsplashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodboardDetailScreen(
    bookId: String,
    title: String,
    onBack: () -> Unit,
    vm: UnsplashViewModel = viewModel()
) {
    // Query beim Öffnen setzen + suchen
    LaunchedEffect(bookId, title) {
        vm.updateQuery(title)
        vm.search()
    }

    val state by vm.ui.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        }
    ) { inner ->
        when {
            state.isLoading -> Box(
                Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            state.error != null -> Box(
                Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { Text("Fehler: ${state.error}") }

            else -> PhotoGrid(
                photos = state.photos,
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun PhotoGrid(photos: List<UnsplashPhoto>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(photos, key = { it.id }) { p ->
            AsyncImage(
                model = p.urls.small,
                contentDescription = p.description ?: p.alt_description ?: "Foto",
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .aspectRatio(1f)
                    .fillMaxWidth()
            )
        }
    }
}
