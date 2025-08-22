package com.example.bookfusion.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bookfusion.model.MoodboardEntry
import com.example.bookfusion.model.UnsplashPhoto
import com.example.bookfusion.viewmodel.MoodboardViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodboardBookIntroScreen(
    bookId: String,
    title: String,
    coverUrl: String,
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    moodboardVM: MoodboardViewModel = viewModel()
) {
    val entries by moodboardVM.entries.collectAsStateWithLifecycle()
    val images: List<UnsplashPhoto> = remember(entries, bookId) {
        entries.firstOrNull { it.bookId == bookId }?.photos.orEmpty()
    }

    val bgTop = Color(0xFFF7ECFF)
    val bgBottom = Color(0xFFEAF2FF)
    val cardTint = Color(0xFFFFF7FE)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moodboard") },
                navigationIcon = { /* dein Back-Icon falls gewünscht */ }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(bgTop, bgBottom)))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = cardTint),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = coverUrl.ifBlank { null },
                        contentDescription = title,
                        modifier = Modifier
                            .size(width = 120.dp, height = 160.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFFEAE6FF))
                    )
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = title, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        AssistChip(onClick = {}, label = {
                            val txt = if (images.isEmpty()) "Noch keine Bilder gespeichert" else "${images.size} Bild${if (images.size == 1) "" else "er"}"
                            Text(txt)
                        })
                        // Hinweis: FilledTonalButton hat KEIN 'leadingIcon'-Param – Icon einfach reinsetzen:
                        FilledTonalButton(onClick = onAddClick) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Bilder hinzufügen")
                        }
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 140.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 84.dp)
            ) {
                items(images, key = { it.id }) { p ->
                    AsyncImage(
                        model = p.urls.small,
                        contentDescription = p.alt_description ?: p.description ?: "Foto",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(22.dp))
                    )
                }
                item(key = "plus-card") {
                    ElevatedCard(
                        onClick = onAddClick,
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF3ECFF)),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(tonalElevation = 4.dp, shape = RoundedCornerShape(14.dp)) {
                                    Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.Add, contentDescription = null)
                                    }
                                }
                                Text("Hinzufügen", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}
