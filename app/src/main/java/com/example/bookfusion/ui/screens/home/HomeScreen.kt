package com.example.bookfusion.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.example.bookapp.model.BookItem
import com.example.bookapp.viewmodel.BookViewModel
import com.example.bookapp.viewmodel.DataState
import com.example.bookfusion.ui.animations.ConfettiEffect
import com.example.bookfusion.ui.screens.home.components.ActionButton
import com.example.bookfusion.ui.screens.home.components.AnimatedGradientTitle
import com.example.bookfusion.ui.screens.home.components.BookDetailSheet
import com.example.bookfusion.ui.screens.home.components.ErrorCard
import com.example.bookfusion.ui.screens.home.components.GlassCard
import com.example.bookfusion.ui.screens.home.components.GlowAura
import com.example.bookfusion.ui.screens.home.components.GlowCTAButton
import com.example.bookfusion.ui.screens.home.components.GradientTitle
import com.example.bookfusion.ui.screens.home.components.ShimmerCard
import com.example.bookfusion.ui.screens.home.components.ShimmerImage
import com.example.bookfusion.ui.screens.home.components.SparkleBackdrop
import com.example.bookfusion.ui.screens.home.components.SwipeableCard
import com.example.bookfusion.ui.screens.home.util.coverUrlFor
import com.example.bookfusion.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

/**
 * Der Hauptscreen der App ("Blind-Date with a Book").
 *
 * - Zeigt ein Start-UI mit Button und animierten Titeln
 * - Wenn gestartet: zufällige Bücher, die per Swipe oder Buttons
 *   "geliked" oder "disliked" werden können
 * - Liked Books werden gespeichert, disliked übersprungen
 * - Konfetti bei neu geladenen Buch-Covern
 * - Klick auf ein Cover öffnet ein Detail-Sheet
 *
 * @param navController Navigation für Wechsel zu anderen Screens
 * @param authViewModel ViewModel für User-Auth
 * @param bookViewModel ViewModel für Bücher (Standard: automatisch erzeugt)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    bookViewModel: BookViewModel = viewModel()
) {
    val user = FirebaseAuth.getInstance().currentUser
    LaunchedEffect(user?.uid) { if (user != null) bookViewModel.loadUserBooks() }

    val book by bookViewModel.bookState.collectAsState()
    val uiState by bookViewModel.uiState.collectAsState()

    var started by remember { mutableStateOf(false) }
    var triggerConfetti by remember { mutableStateOf(false) }

    var showDetail by remember { mutableStateOf(false) }
    var detailBook by remember { mutableStateOf<BookItem?>(null) }

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val excludedIds = remember { mutableStateListOf<String>() }
    var retryAttempts by remember { mutableStateOf(0) }

    LaunchedEffect(book?.id) {
        val currentId = book?.id ?: return@LaunchedEffect
        if (excludedIds.contains(currentId)) {
            if (retryAttempts < 5) {
                retryAttempts++
                bookViewModel.loadRandomBook()
            }
        } else {
            retryAttempts = 0
        }
    }

    val accent = Color(0xFF2A1A5E)
    val bgTop = Color(0xFFF4E8FB)
    val bgBottom = Color(0xFFE2CFF8)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(bgTop, bgBottom)))
                .padding(paddingValues)
        ) {
            SparkleBackdrop()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = if (!started) Arrangement.Center else Arrangement.Top
            ) {
                if (!started) {
                    Spacer(Modifier.height(8.dp))
                    AnimatedGradientTitle(
                        textTop = "Blind-Date",
                        textBottom = "with a Book",
                        accent = accent,
                        secondary = bgBottom
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Swipe für dein Buch, um den richtigen zu finden",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF6F6F6F),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(32.dp))
                    GlowAura(auraSize = 220.dp, color = accent.copy(alpha = 0.35f))
                    Spacer(Modifier.height(16.dp))
                    GlowCTAButton(
                        text = "🎁 Starte dein Blind-Date",
                        container = accent
                    ) {
                        started = true
                        bookViewModel.loadRandomBook()
                    }
                } else {
                    Spacer(Modifier.height(20.dp))
                    GradientTitle(
                        text = "Blind-Date with a Book",
                        gradient = Brush.linearGradient(listOf(accent, bgBottom)),
                        letterSpacing = 0.6.sp,
                        fontSize = 26.sp
                    )
                    Spacer(Modifier.height(15.dp))
                    Text(
                        "Wische nach ❤️ oder ✖",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF7C7C7C)
                    )

                    Spacer(Modifier.height(20.dp))

                    when (uiState) {
                        DataState.LOADING -> ShimmerCard()
                        DataState.ERROR -> ErrorCard(onRetry = { bookViewModel.loadRandomBook() })
                        DataState.READY -> book?.let { b ->
                            val imageUrl = coverUrlFor(b, preferZoom = 1)

                            GlassCard {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .shadow(
                                                elevation = 10.dp,
                                                shape = RoundedCornerShape(24.dp),
                                                clip = false
                                            )
                                            .clip(RoundedCornerShape(24.dp))
                                            .background(Color.White.copy(alpha = 0.35f))
                                            .border(
                                                width = 1.dp,
                                                color = Color.White.copy(alpha = 0.35f),
                                                shape = RoundedCornerShape(24.dp)
                                            )
                                    )

                                    key(b.id) {
                                        SwipeableCard(
                                            onSwiped = {
                                                excludedIds.add(b.id)
                                                bookViewModel.loadRandomBook()
                                            },
                                            modifier = Modifier
                                                .width(260.dp)
                                                .height(400.dp)
                                                .clip(RoundedCornerShape(20.dp))
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                if (imageUrl.isNullOrBlank()) {
                                                    ShimmerImage(
                                                        width = 220.dp,
                                                        height = 330.dp,
                                                        radius = 12.dp
                                                    )
                                                } else {
                                                    SubcomposeAsyncImage(
                                                        model = imageUrl,
                                                        contentDescription = b.volumeInfo.title,
                                                        contentScale = ContentScale.Crop,
                                                        loading = {
                                                            ShimmerImage(
                                                                width = 220.dp,
                                                                height = 330.dp,
                                                                radius = 12.dp
                                                            )
                                                        },
                                                        modifier = Modifier
                                                            .width(220.dp)
                                                            .height(330.dp)
                                                            .clip(RoundedCornerShape(12.dp))
                                                            .clickable {
                                                                detailBook = b
                                                                showDetail = true
                                                            },
                                                        onSuccess = { triggerConfetti = true }
                                                    )
                                                }
                                                Spacer(Modifier.height(8.dp))
                                                Text(
                                                    text = b.volumeInfo.title,
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.SemiBold
                                                    ),
                                                    color = accent,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.padding(horizontal = 10.dp),
                                                    maxLines = 2
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(25.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(48.dp, Alignment.CenterHorizontally),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        ActionButton(
                            icon = Icons.Filled.Close,
                            contentDescription = "Dislike",
                            ring = listOf(Color(0x1A2A1A5E), Color(0x332A1A5E)),
                            iconTint = accent,
                            background = Color.White,
                            iconSize = 32.dp,
                            buttonSize = 78.dp
                        ) {
                            book?.id?.let { excludedIds.add(it) }
                            bookViewModel.loadRandomBook()
                        }

                        ActionButton(
                            icon = Icons.Filled.Favorite,
                            contentDescription = "Like",
                            ring = listOf(Color(0x662A1A5E), Color(0xFF2A1A5E)),
                            iconTint = Color.White,
                            background = accent,
                            borderColor = Color.Transparent,
                            iconSize = 32.dp,
                            buttonSize = 78.dp
                        ) {
                            book?.let {
                                bookViewModel.likeBook(it)
                                excludedIds.add(it.id)
                                scope.launch {
                                    snackbar.showSnackbar("✨ „${it.volumeInfo.title}“ steht jetzt in deinem Regal!")
                                }
                                bookViewModel.loadRandomBook()
                            }
                        }
                    }
                }
            }

            ConfettiEffect(
                show = triggerConfetti,
                onFinished = { triggerConfetti = false }
            )

            val current = detailBook
            if (showDetail && current != null) {
                BookDetailSheet(
                    volumeId = current.id,
                    visible = true,
                    onDismiss = { showDetail = false },
                    fallback = current,
                    accent = accent
                )
            }
        }
    }
}
