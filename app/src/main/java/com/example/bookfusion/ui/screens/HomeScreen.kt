package com.example.bookfusion.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookapp.viewmodel.BookViewModel
import com.example.bookapp.viewmodel.DataState
import com.example.bookfusion.ui.components.SwipeableCard
import com.example.bookfusion.viewmodel.AuthViewModel
import com.example.bookfusion.ui.animations.ConfettiEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    bookViewModel: BookViewModel = viewModel()
) {
    val book by bookViewModel.bookState.collectAsState()
    val uiState by bookViewModel.uiState.collectAsState()

    var started by remember { mutableStateOf(false) }
    var triggerConfetti by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
            if (!started) {
                Text(
                    "Bereit für dein Buch-Schicksal?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = iconColor
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        started = true
                        bookViewModel.loadRandomBook()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = iconColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(60.dp)
                        .width(240.dp)
                ) {
                    Text("🎁 Starte dein Blind-Date", color = Color.White)
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, bottom = 24.dp)
                ) {
                    Text(
                        text = "Blind-Date with a Book",
                        style = MaterialTheme.typography.headlineLarge,
                        color = iconColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Swipe für dein Buch, um den richtigen zu finden",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(70.dp))

                when (uiState) {
                    DataState.LOADING -> CircularProgressIndicator()
                    DataState.ERROR -> Text("Fehler beim Laden", color = Color.Red)
                    DataState.READY -> book?.let {
                        val imageUrl =
                            it.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")
                        Log.d("ImageURL", "📷 $imageUrl")

                        Box(
                            modifier = Modifier
                                .height(480.dp)
                                .width(280.dp)
                        ) {
                            SwipeableCard(
                                onSwiped = { bookViewModel.loadRandomBook() },
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = it.volumeInfo.title,
                                        modifier = Modifier
                                            .height(400.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .shadow(10.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = it.volumeInfo.title,
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = iconColor,
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                        maxLines = 2
                                    )
                                }
                            }

                            ConfettiEffect(show = triggerConfetti)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {
                            triggerConfetti = true
                            bookViewModel.loadRandomBook()
                            scope.launch {
                                delay(1500)
                                triggerConfetti = false

                            }
                        },
                        modifier = Modifier
                            .size(80.dp)
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
                            .size(80.dp)
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
}
