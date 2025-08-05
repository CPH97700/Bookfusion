package com.example.bookfusion.ui.animations

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random
import androidx.compose.animation.core.Animatable


@Composable
fun ConfettiEffect(show: Boolean) {
    if (!show) return

    val colors = listOf(
        Color(0xFFE57373), // Red
        Color(0xFF81C784), // Green
        Color(0xFF64B5F6), // Blue
        Color(0xFFFFD54F), // Yellow
        Color(0xFFBA68C8), // Purple
        Color(0xFFFF8A65)  // Orange
    )

    val particleCount = 40
    val particles = remember { List(particleCount) { Animatable(0f) } }
    val random = remember { Random(System.currentTimeMillis()) }

    Box(modifier = Modifier.fillMaxWidth()) {
        particles.forEachIndexed { index, anim ->
            val color = colors[index % colors.size]
            val offsetX = random.nextInt(0, 300).dp
            val size = random.nextInt(6, 14).dp

            LaunchedEffect(anim) {
                delay(index * 20L)
                anim.snapTo(0f)
                anim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 1200 + random.nextInt(300),
                        easing = LinearEasing
                    )
                )
            }

            Box(
                modifier = Modifier
                    .offset(x = offsetX, y = (anim.value * 700f).dp)
                    .size(size)
                    .background(color = color, shape = CircleShape)
            )
        }
    }
}
