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
import androidx.compose.ui.graphics.toArgb
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Rotation
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit


@Composable
fun ConfettiEffect(show: Boolean) {
    val party = remember {
        Party(
            emitter = Emitter(duration = 3, TimeUnit.SECONDS).perSecond(50),
            spread = 360,
            colors = listOf(Color.Red.toArgb(),Color.Blue.toArgb(),Color.Green.toArgb(),Color.Yellow.toArgb()),
            rotation = Rotation.enabled(),
            fadeOutEnabled = true
        )
    }
    if (show) {
        KonfettiView(
            modifier = Modifier.fillMaxSize(),
            parties = listOf(party)
        )
    }
}