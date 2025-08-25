package com.example.bookfusion.ui.screens.home.components


import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
/**
 * Eine Karte mit Glas-Effekt (halb transparent, mit Glanz).
 *
 * @param content Inhalt, der in die Karte eingesetzt wird.
 */
@Composable
fun GlassCard(content: @Composable BoxScope.() -> Unit) {
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
fun ShimmerCard() {
    GlassCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp),
            contentAlignment = Alignment.Center
        ) {
            ShimmerImage(width = 220.dp, height = 330.dp, radius = 12.dp)
        }
    }
}

@Composable
fun ShimmerImage(
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
fun ErrorCard(onRetry: () -> Unit) {
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
