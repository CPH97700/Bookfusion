package com.example.bookfusion.ui.screens.home.components


import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SparkleBackdrop() {
    val dots = 16
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
