package com.example.bookfusion.ui.animations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.foundation.layout.fillMaxSize
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Rotation
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun ConfettiEffect(
    show: Boolean,
    onFinished: () -> Unit = {}
) {
    val palette = remember {
        listOf(
            Color(0xFFEEDBE9).toArgb(),
            Color(0xFFF5E6F2).toArgb(),
            Color(0xFFEFD3F5).toArgb(),
            Color(0xFFD2B7E5).toArgb(),
            Color(0xFFCBB2E9).toArgb(),
            Color(0xFFB39DDB).toArgb(),
            Color(0xFF2A1A5E).toArgb()
        )
    }

    val emissionDurationMs = 3500L
    val particlesPerSec = 200

    val party = remember {
        Party(
            emitter = Emitter(duration = emissionDurationMs, TimeUnit.MILLISECONDS)
                .perSecond(particlesPerSec),
            position = Position.Relative(0.5, 0.0),
            spread = 100,
            angle = 270,
            rotation = Rotation.enabled(),
            fadeOutEnabled = true,
            colors = palette
        )
    }

    if (show) {
        KonfettiView(
            modifier = Modifier.fillMaxSize(),
            parties = listOf(party)
        )

        LaunchedEffect(show) {
            kotlinx.coroutines.delay(emissionDurationMs + 800L)
            onFinished()
        }
    }
}
