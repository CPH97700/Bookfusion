package com.example.bookfusion.ui.screens.loginSignup

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * **StartScreen** – Erster Screen beim Öffnen der App.
 *
 * Features:
 * - 🎨 Hintergrund mit Farbverlauf (lila → rosa)
 * - 👆 Wischen nach oben (Swipe/Drag) führt automatisch zur Login-Seite
 * - 📚 Text mit Hinweis „BookFusion wartet – wisch nach oben“
 * - ⬆️ Mehrere animierte Pfeile nach oben zur visuellen Unterstützung
 *
 * @param navController Navigation, um nach dem Wischen zur Login-Seite zu wechseln
 */
@Composable
fun StartScreen(navController: NavController) {
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(
                listOf(Color(0xFFB18DD6), Color(0xFFE4C1F9))
            ))
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    offsetY += dragAmount
                    if (offsetY < -150f) {
                        navController.navigate("login")
                    }
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("BookFusion wartet –\nwisch nach oben 📚", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E2F6D), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
        }
    }
}
