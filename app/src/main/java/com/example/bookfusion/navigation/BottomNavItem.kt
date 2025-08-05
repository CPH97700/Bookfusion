package com.example.bookfusion.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Create
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home", Icons.Filled.Book, "Home")
    object Journal : BottomNavItem("journal", Icons.Filled.Create, "Journal")
    object Moodboard : BottomNavItem("moodboard", Icons.Filled.AutoAwesomeMosaic, "Moodboard")
}