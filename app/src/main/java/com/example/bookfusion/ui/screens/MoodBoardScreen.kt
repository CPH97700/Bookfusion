package com.example.bookfusion.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
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
import com.example.bookfusion.viewmodel.MoodboardViewModel
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodBoardScreen(
    entriesFlow: StateFlow<List<MoodboardEntry>>,
    onOpenMoodboard: (bookId: String, title: String) -> Unit,
    modifier: Modifier = Modifier,
    moodboardVM: MoodboardViewModel = viewModel()
) {
    val entries by entriesFlow.collectAsState()

    val bgTop = Color(0xFFF7ECFF)
    val bgBottom = Color(0xFFEAF2FF)

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var pending by remember { mutableStateOf<MoodboardEntry?>(null) }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Moodboard") }) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { inner ->
        Box(
            modifier = modifier
                .padding(inner)
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(bgTop, bgBottom)))
        ) {
            if (entries.isEmpty()) {
                EmptyState(
                    title = "Noch nichts hier …",
                    subtitle = "Füge Bücher aus deinem Journal hinzu, um Moodboards zu bauen.",
                    modifier = Modifier.align(Alignment.Center).padding(horizontal = 24.dp)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(entries, key = { it.bookId }) { e ->
                        MoodboardLibraryCard(
                            title = e.title,
                            coverUrl = e.coverUrl,
                            count = e.photos.size,
                            onOpen = { onOpenMoodboard(e.bookId, e.title) },
                            onDelete = { pending = e }
                        )
                    }
                }
            }
        }
    }

    if (pending != null) {
        val del = pending!!
        AlertDialog(
            onDismissRequest = { pending = null },
            title = { Text("Buch löschen?") },
            text = { Text("„${del.title}“ und sein Moodboard werden entfernt. Das kann nicht rückgängig gemacht werden.") },
            icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
            confirmButton = {
                TextButton(onClick = {
                    pending = null
                    moodboardVM.deleteEntry(del.bookId)   // <-- statt deleteBook(...)
                    scope.launch { snackbar.showSnackbar("„${del.title}“ gelöscht") }
                }) { Text("Löschen") }
            },
            dismissButton = { TextButton(onClick = { pending = null }) { Text("Abbrechen") } }
        )
    }
}

@Composable
private fun MoodboardLibraryCard(
    title: String,
    coverUrl: String,
    count: Int,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    ElevatedCard(
        onClick = onOpen,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFFFF7FE)),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            Box {
                AsyncImage(
                    model = coverUrl.ifBlank { null },
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFEAE6FF))
                )
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)) {
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Mehr")
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(
                            text = { Text("Löschen") },
                            leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                            onClick = { menuOpen = false; onDelete() }
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(text = title, style = MaterialTheme.typography.titleSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (count > 0) {
                Spacer(Modifier.height(8.dp))
                AssistChip(onClick = onOpen, label = { Text("$count Foto${if (count == 1) "" else "s"}") })
            }
        }
    }
}

@Composable
private fun EmptyState(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(96.dp).clip(RoundedCornerShape(28.dp)).background(Color(0xFFEAE6FF))
        )
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
