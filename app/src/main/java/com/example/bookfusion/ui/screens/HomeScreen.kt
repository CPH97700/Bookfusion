package com.example.bookfusion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookfusion.viewmodel.AuthViewModel

// 🏠 HomeScreen.kt – Dummy nach Login mit Logout-Navigation

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Willkommen bei BookFusion 📚", fontSize = 22.sp)

        Spacer(modifier = Modifier.height(24.dp))

        // 🚪 Logout-Button → navigiert zurück zum StartScreen
        Button(onClick = {
            viewModel.logout()
            navController.navigate("start") {
                popUpTo("home") { inclusive = true } // entfernt Home aus BackStack
            }
        }) {
            Text("Logout")
        }
    }
}
