package com.example.bookfusion.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.example.bookapp.viewmodel.BookViewModel
import com.example.bookapp.viewmodel.DataState
import com.example.bookfusion.ui.animations.ConfettiEffect
import com.example.bookfusion.ui.components.SwipeableCard
import com.example.bookfusion.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.min

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
        // ✅ Keine TopBar – stattdessen ein Settings‑FAB
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
                    modifier = Modifier.size(26.dp)
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
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!started) {
                    Spacer(Modifier.height(8.dp))
                    AnimatedGradientTitle(
                        textTop = "Blind‑Date",
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
                    Spacer(Modifier.height(28.dp))
                    GlowAura(auraSize = 220.dp, color = accent.copy(alpha = 0.35f))
                    Spacer(Modifier.height(12.dp))
                    GlowCTAButton(
                        text = "🎁 Starte dein Blind‑Date",
                        container = accent
                    ) {
                        started = true
                        bookViewModel.loadRandomBook()
                    }
                    Spacer(Modifier.height(32.dp))
                } else {
                    GradientTitle(
                        text = "Blind‑Date with a Book",
                        gradient = Brush.linearGradient(listOf(accent, bgBottom)),
                        letterSpacing = 0.6.sp,
                        fontSize = 26.sp
                    )
                    Spacer(Modifier.height(8.dp))
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
                            val imageUrl = b.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")

                            // 📚 REALISTISCHE COVER-KARTE (zentriert, 2:3 Cover)
                            GlassCard {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    SwipeableCard(
                                        onSwiped = {
                                            book?.let { swiped -> bookViewModel.likeBook(swiped) }
                                            bookViewModel.loadRandomBook()
                                        },
                                        modifier = Modifier
                                            .width(260.dp)               // Gesamte Karte kompakt
                                            .height(400.dp)
                                            .clip(RoundedCornerShape(20.dp))
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            // 👉 echtes Buch-Cover: 2:3 Verhältnis (220 x 330)
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
                                            Spacer(Modifier.height(12.dp))
                                            Text(
                                                text = b.volumeInfo.title,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
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

                    Spacer(Modifier.height(16.dp))

                    // 🔘 schlanke Action‑Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ActionButton(
                            icon = Icons.Filled.Close,
                            contentDescription = "Dislike",
                            ring = listOf(Color(0x1A2A1A5E), Color(0x332A1A5E)),
                            iconTint = accent,
                            background = Color.White
                        ) {
                            bookViewModel.loadRandomBook()
                        }

                        ActionButton(
                            icon = Icons.Filled.Favorite,
                            contentDescription = "Like",
                            ring = listOf(Color(0x662A1A5E), Color(0xFF2A1A5E)),
                            iconTint = Color.White,
                            background = accent,
                            borderColor = Color.Transparent
                        ) {
                            book?.let {
                                bookViewModel.likeBook(it)
                                bookViewModel.loadRandomBook()
                            }
                        }
                    }
                }
            }

            // 🎉 Konfetti
            ConfettiEffect(
                show = triggerConfetti,
                onFinished = { triggerConfetti = false }
            )
        }
    }
}

/* ---------- UI‑Bausteine ---------- */

@Composable
private fun GlassCard(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(22.dp, RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.70f),
                        Color.White.copy(alpha = 0.55f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.9f),
                        Color(0xFFD7C9FF).copy(alpha = 0.6f)
                    )
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(14.dp)
    ) { content() }
}

@Composable
private fun ShimmerCard() {
    GlassCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp),
            contentAlignment = Alignment.Center
        ) { ShimmerImage(width = 220.dp, height = 330.dp, radius = 12.dp) }
    }
}

@Composable
private fun ShimmerImage(
    width: Dp = 220.dp,
    height: Dp = 330.dp,
    radius: Dp = 12.dp
) {
    val shimmer = rememberInfiniteTransition(label = "shimmer")
    val x by shimmer.animateFloat(
        initialValue = -600f, targetValue = 600f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing)),
        label = "x"
    )
    val brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFEBE3F9),
            Color(0xFFF6F1FF),
            Color(0xFFEBE3F9)
        ),
        start = Offset(x, 0f),
        end = Offset(x + 300f, 0f)
    )
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(radius))
            .background(brush)
    )
}

@Composable
private fun ErrorCard(onRetry: () -> Unit) {
    GlassCard {
        Column(
            Modifier
                .fillMaxWidth()
                .height(300.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Ups – konnte kein Buch laden.", color = Color(0xFF7C7C7C))
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRetry) { Text("Nochmal versuchen") }
        }
    }
}

@Composable
private fun GradientTitle(
    text: String,
    gradient: Brush,
    letterSpacing: TextUnit = 0.sp,
    fontSize: TextUnit = 32.sp
) {
    val styled = remember(text, gradient, letterSpacing, fontSize) {
        buildAnnotatedString {
            withStyle(
                SpanStyle(
                    brush = gradient,
                    letterSpacing = letterSpacing,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold
                )
            ) { append(text) }
        }
    }
    Text(text = styled, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
}

@Composable
private fun AnimatedGradientTitle(
    textTop: String,
    textBottom: String,
    accent: Color,
    secondary: Color
) {
    val colors = listOf(accent, secondary, accent)
    val transition = rememberInfiniteTransition(label = "titleAnim")
    val shift by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing), RepeatMode.Reverse),
        label = "shift"
    )
    val brush = Brush.linearGradient(
        colors = colors,
        start = Offset.Zero,
        end = Offset(1000f * (0.4f + shift), 0f)
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        GradientTitle(textTop, brush, 1.0.sp, 30.sp)
        Spacer(Modifier.height(2.dp))
        GradientTitle(textBottom, brush, 1.0.sp, 38.sp)
    }
}

@Composable
private fun GlowAura(auraSize: Dp, color: Color) {
    val transition = rememberInfiniteTransition(label = "glow")
    val alpha by transition.animateFloat(
        initialValue = 0.35f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glowAlpha"
    )
    Box(
        modifier = Modifier
            .size(auraSize)
            .alpha(alpha)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radiusPx = min(size.width, size.height) * 0.6f
            drawIntoCanvas {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color, Color.Transparent),
                        center = center,
                        radius = radiusPx
                    ),
                    radius = radiusPx
                )
            }
        }
    }
}

@Composable
private fun GlowCTAButton(text: String, container: Color, onClick: () -> Unit) {
    val transition = rememberInfiniteTransition(label = "ctaGlow")
    val glow by transition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = container),
        shape = RoundedCornerShape(26.dp),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp),
        modifier = Modifier
            .height(56.dp)
            .shadow(12.dp, RoundedCornerShape(26.dp))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = glow * 0.6f),
                shape = RoundedCornerShape(26.dp)
            )
    ) {
        Text(text, color = Color.White, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun SparkleBackdrop() {
    val dots = remember { 16 }
    val anim = rememberInfiniteTransition(label = "sparkles")
    val drift by anim.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "drift"
    )
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
    ) {
        val w = size.width
        val h = size.height
        repeat(dots) { i ->
            val x = (w / dots) * i + (drift * 30f)
            val y = (h / dots) * (i % (dots / 2)) + (drift * 20f)
            drawCircle(
                color = Color.White.copy(alpha = 0.06f),
                radius = 10f + (i % 3) * 4f,
                center = Offset(x % w, (y + i * 20) % h)
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    contentDescription: String,
    ring: List<Color>,
    iconTint: Color,
    background: Color,
    borderColor: Color = Color.Transparent,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.94f else 1f, label = "pressScale")

    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.Transparent,
        shadowElevation = 10.dp,
        tonalElevation = 0.dp,
        interactionSource = interaction
    ) {
        Box(
            modifier = Modifier
                .size(70.dp) // kompakter Button
                .background(brush = Brush.linearGradient(ring), shape = CircleShape)
                .padding(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(background, CircleShape)
                    .border(
                        width = if (borderColor == Color.Transparent) 0.dp else 2.dp,
                        color = borderColor,
                        shape = CircleShape
                    )
                    .padding(14.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = iconTint,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}
