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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookapp.viewmodel.BookViewModel
import com.example.bookapp.viewmodel.DataState
import com.example.bookfusion.ui.animations.ConfettiEffect
import com.example.bookfusion.ui.components.SwipeableCard
import com.example.bookfusion.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.min

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    bookViewModel: BookViewModel = viewModel()
) {
    val user = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(user?.uid) {
        if (user != null) bookViewModel.loadUserBooks()
    }

    val book by bookViewModel.bookState.collectAsState()
    val uiState by bookViewModel.uiState.collectAsState()

    var started by remember { mutableStateOf(false) }
    var triggerConfetti by remember { mutableStateOf(false) }

    val iconColor = Color(0xFF2A1A5E)
    val buttonColor = Color.White
    val bgTop = Color(0xFFEEDBE9)
    val bgBottom = Color(0xFFD2B7E5)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgTop, bgBottom)))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        SparkleBackdrop()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!started) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AnimatedGradientTitle(
                            textTop = "Bereit für dein",
                            textBottom = "Buch‑Schicksal?",
                            accent = iconColor,
                            secondary = bgBottom
                        )

                        Spacer(Modifier.height(28.dp))
                        GlowAura(auraSize = 220.dp, color = iconColor.copy(alpha = 0.35f))

                        GlowCTAButton(
                            text = "🎁 Starte dein Blind‑Date",
                            container = iconColor,
                            onClick = {
                                started = true
                                bookViewModel.loadRandomBook()
                            }
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 8.dp)
                ) {
                    GradientTitle(
                        text = "Blind‑Date with a Book",
                        gradient = Brush.linearGradient(listOf(iconColor, bgBottom)),
                        letterSpacing = 0.8.sp,
                        fontSize = 28.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Swipe für dein Buch, um den richtigen zu finden",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF6F6F6F),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(28.dp))

                when (uiState) {
                    DataState.LOADING -> CircularProgressIndicator()
                    DataState.ERROR -> Text("Fehler beim Laden", color = Color.Red)
                    DataState.READY -> book?.let { b ->
                        val imageUrl = b.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")

                        Box(
                            modifier = Modifier
                                .width(320.dp)
                                .height(500.dp)
                                .shadow(18.dp, RoundedCornerShape(28.dp))
                                .clip(RoundedCornerShape(28.dp))
                                .background(Color.White)
                                .padding(12.dp)
                        ) {
                            SwipeableCard(
                                onSwiped = {
                                    book?.let { swiped -> bookViewModel.likeBook(swiped) }
                                    bookViewModel.loadRandomBook()
                                },
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = b.volumeInfo.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(410.dp)
                                            .clip(RoundedCornerShape(22.dp)),
                                        onSuccess = {
                                            triggerConfetti = true
                                        }
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        text = b.volumeInfo.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = iconColor,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(22.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(28.dp, Alignment.CenterHorizontally),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ActionButton(
                        icon = Icons.Filled.Close,
                        contentDescription = "Dislike",
                        ring = listOf(Color(0x332A1A5E), Color(0x802A1A5E)),
                        iconTint = iconColor,
                        background = buttonColor
                    ) {
                        bookViewModel.loadRandomBook()
                    }

                    ActionButton(
                        icon = Icons.Filled.Favorite,
                        contentDescription = "Like",
                        ring = listOf(Color(0x662A1A5E), Color(0xFF2A1A5E)),
                        iconTint = iconColor,
                        background = buttonColor,
                        borderColor = iconColor
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

/* === Zusatz-UI (WOW) === */

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
                    fontSize = fontSize
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
        animationSpec = infiniteRepeatable(tween(2800, easing = LinearEasing), RepeatMode.Reverse),
        label = "shift"
    )
    val brush = Brush.linearGradient(
        colors = colors,
        start = Offset.Zero,
        end = Offset(1000f * (0.4f + shift), 0f)
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        GradientTitle(textTop, brush, 1.2.sp, 34.sp)
        Spacer(Modifier.height(2.dp))
        GradientTitle(textBottom, brush, 1.2.sp, 44.sp)
    }
}

/** ⚙️ Fix: Name geändert (keine Kollision mit DrawScope.size) und Radius in PX berechnet */
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
            // DrawScope.size = Pixel; kein toPx() nötig
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
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val lift by animateDpAsState(if (pressed) 2.dp else 8.dp, label = "lift")

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = container),
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .height(56.dp)
            .width(260.dp)
            .shadow(lift, RoundedCornerShape(28.dp)),
        interactionSource = interaction,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(text, color = Color.White, style = MaterialTheme.typography.titleMedium)
    }
}

/** Dezente funkelnde Kreise im Hintergrund für Tiefe. */
@Composable
private fun SparkleBackdrop() {
    val dots = remember { 18 }
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
                color = Color.White.copy(alpha = 0.05f),
                radius = 10f + (i % 3) * 4f,
                center = Offset(x % w, (y + i * 20) % h)
            )
        }
    }
}

/** ActionButton – Signatur passt zu den Aufrufen oben. */
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
        interactionSource = interaction
    ) {
        Box(
            modifier = Modifier
                .size(86.dp)
                .background(brush = Brush.linearGradient(ring), shape = CircleShape)
                .padding(3.dp)
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
                    .padding(18.dp)
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
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}
