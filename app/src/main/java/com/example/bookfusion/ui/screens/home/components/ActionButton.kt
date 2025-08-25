package com.example.bookfusion.ui.screens.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
/**
 * Ein runder Button mit Icon in der Mitte.
 *
 * Features:
 * - Farbiger Ring außen (Gradient)
 * - Hintergrundfarbe innen
 * - Optionaler Rand
 * - Drück-Animation (Button wird kurz kleiner)
 *
 * @param icon Das Icon, das in der Mitte angezeigt wird
 * @param contentDescription Text für Screenreader
 * @param ring Farben für den äußeren Ring (Gradient)
 * @param iconTint Farbe des Icons
 * @param background Hintergrundfarbe des Buttons
 * @param borderColor Optionaler Rand (Standard: transparent)
 * @param iconSize Größe des Icons (Standard: 26.dp)
 * @param buttonSize Gesamtgröße des Buttons (Standard: 70.dp)
 * @param onClick Aktion, die beim Klicken ausgeführt wird
 */
@Composable
fun ActionButton(
    icon: ImageVector,
    contentDescription: String,
    ring: List<Color>,
    iconTint: Color,
    background: Color,
    borderColor: Color = Color.Transparent,
    iconSize: Dp = 26.dp,
    buttonSize: Dp = 70.dp,
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
                .size(buttonSize)
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
                    .padding(buttonSize * 0.20f)
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
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}
