package com.example.bookfusion.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.bookapp.viewmodel.BookViewModel
import com.example.bookapp.viewmodel.DataState
import com.example.bookfusion.ui.animations.ConfettiEffect
import com.example.bookfusion.ui.components.SwipeableCard
import com.example.bookfusion.ui.screens.home.components.ActionButton
import com.example.bookfusion.ui.screens.home.components.AnimatedGradientTitle
import com.example.bookfusion.ui.screens.home.components.ErrorCard
import com.example.bookfusion.ui.screens.home.components.GlassCard
import com.example.bookfusion.ui.screens.home.components.GlowAura
import com.example.bookfusion.ui.screens.home.components.GlowCTAButton
import com.example.bookfusion.ui.screens.home.components.GradientTitle
import com.example.bookfusion.ui.screens.home.components.ShimmerCard
import com.example.bookfusion.ui.screens.home.components.ShimmerImage
import com.example.bookfusion.ui.screens.home.components.SparkleBackdrop
import com.example.bookfusion.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

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

    val accent = Color(0xFF2A1A5E)
    val bgTop = Color(0xFFF4E8FB)
    val bgBottom = Color(0xFFE2CFF8)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("settings") },
                containerColor = accent,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Einstellungen",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
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
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                    Spacer(Modifier.height(8.dp))
                    GradientTitle(
                        text = "Blind-Date with a Book",
                        gradient = Brush.linearGradient(listOf(accent, bgBottom)),
                        letterSpacing = 0.6.sp,
                        fontSize = 26.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Wische nach ❤️ oder ✖",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF7C7C7C)
                    )
                    Spacer(Modifier.height(6.dp))

                    when (uiState) {
                        DataState.LOADING -> ShimmerCard()
                        DataState.ERROR -> ErrorCard(onRetry = { bookViewModel.loadRandomBook() })
                        DataState.READY -> book?.let { b ->
                            val imageUrl = b.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")

                            GlassCard {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    SwipeableCard(
                                        onSwiped = {
                                            book?.let { swiped -> bookViewModel.likeBook(swiped) }
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
                                            SubcomposeAsyncImage(
                                                model = imageUrl,
                                                contentDescription = b.volumeInfo.title,
                                                contentScale = ContentScale.Crop,
                                                loading = { ShimmerImage(width = 220.dp, height = 330.dp, radius = 12.dp) },
                                                modifier = Modifier
                                                    .width(220.dp)
                                                    .height(330.dp)
                                                    .clip(RoundedCornerShape(12.dp)),
                                                onSuccess = { triggerConfetti = true }
                                            )
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

                    // Buttons näher an der Card
                    Spacer(Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(48.dp, Alignment.CenterHorizontally),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp) // leichter Sicherheitsabstand zur BottomBar
                    ) {
                        ActionButton(
                            icon = Icons.Filled.Close,
                            contentDescription = "Dislike",
                            ring = listOf(Color(0x1A2A1A5E), Color(0x332A1A5E)),
                            iconTint = accent,
                            background = Color.White,
                            iconSize = 32.dp,        // << größer
                            buttonSize = 78.dp       // << etwas größeres Button-Gesamtformat
                        ) {
                            bookViewModel.loadRandomBook()
                        }

                        ActionButton(
                            icon = Icons.Filled.Favorite,
                            contentDescription = "Like",
                            ring = listOf(Color(0x662A1A5E), Color(0xFF2A1A5E)),
                            iconTint = Color.White,
                            background = accent,
                            borderColor = Color.Transparent,
                            iconSize = 32.dp,        // << größer
                            buttonSize = 78.dp
                        ) {
                            book?.let {
                                bookViewModel.likeBook(it)
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
        }
    }
}
