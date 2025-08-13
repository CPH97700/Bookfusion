package com.example.bookfusion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.bookfusion.viewmodel.UnsplashViewModel
import com.example.bookfusion.model.UnsplashPhoto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodBoardScreen(
    modifier: Modifier = Modifier,
    vm: UnsplashViewModel = viewModel() // holt dein bestehendes ViewModel
) {
    val state by vm.ui.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Moodboard") })
        }
    ) { inner ->
        Column(
            modifier = modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = vm::updateQuery,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Unsplash Suche") },
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = vm::search) { Text("Suchen") }

            Spacer(Modifier.height(16.dp))

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                state.error != null -> Text("Fehler: ${state.error}")
                else -> PhotoGrid(photos = state.photos)
            }
        }
    }
}

@Composable
private fun PhotoGrid(photos: List<UnsplashPhoto>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(photos, key = { it.id }) { photo ->
            AsyncImage(
                model = photo.urls.small,
                contentDescription = photo.description ?: photo.alt_description ?: "Foto",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
        }
    }
}
