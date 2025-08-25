package com.example.bookfusion.ui.screens.home.components


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue

/**
 * Zeigt einen Text mit einem Farbverlauf (Gradient) an.
 *
 * @param text Der anzuzeigende Text
 * @param gradient Farbverlauf, der über den Text gelegt wird
 * @param letterSpacing Abstand zwischen den Buchstaben (Standard: 0)
 * @param fontSize Schriftgröße (Standard: 32sp)
 */
@Composable
fun GradientTitle(
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
fun AnimatedGradientTitle(
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
