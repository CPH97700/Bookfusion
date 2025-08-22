package com.example.bookfusion.ui.screens.home.components


import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun GlowAura(auraSize: Dp, color: Color) {
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
fun GlowCTAButton(text: String, container: Color, onClick: () -> Unit) {
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
