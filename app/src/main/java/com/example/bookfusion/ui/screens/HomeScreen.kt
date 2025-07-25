package com.example.bookfusion.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Close
import androidx.navigation.NavController
import com.example.bookfusion.viewmodel.AuthViewModel
import android.util.Log
import com.example.bookapp.viewmodel.BookViewModel
import com.example.bookapp.viewmodel.DataState

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    bookViewModel: BookViewModel = viewModel()
) {
    val book by bookViewModel.bookState.collectAsState()
    val uiState by bookViewModel.uiState.collectAsState()

    val backgroundColor = Color(0xFFEEDBE9)
    val buttonColor = Color.White
    val iconColor = Color(0xFF2A1A5E)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFEEDBE9), Color(0xFFD2B7E5))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔖 Überschrift
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    Modifier
                        .padding(top = 32.dp, bottom = 16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Blind-Date with a Book",
                        style = MaterialTheme.typography.headlineMedium,
                        color = iconColor
                    )
                    Text(
                        "Swipe für dein Buch um den richtigen zu finden",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 📖 Buch anzeigen
            when (uiState) {
                DataState.LOADING -> CircularProgressIndicator()
                DataState.ERROR -> Text("Fehler beim Laden", color = Color.Red)
                DataState.READY -> book?.let {
                    val imageUrl = it.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")
                    Log.d("ImageURL", "📷 $imageUrl")

                    AsyncImage(
                        model = imageUrl,
                        contentDescription = it.volumeInfo.title,
                        modifier = Modifier
                            .height(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .shadow(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ❤️ / ❌ Buttons
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { bookViewModel.loadRandomBook() },
                    modifier = Modifier
                        .size(64.dp)
                        .background(buttonColor, shape = CircleShape)
                        .border(2.dp, Color.DarkGray, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dislike",
                        tint = iconColor,
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(
                    onClick = { /* TODO: Like speichern */ },
                    modifier = Modifier
                        .size(64.dp)
                        .background(buttonColor, shape = CircleShape)
                        .border(2.dp, iconColor, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = iconColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
