// app/src/main/java/com/example/bookfusion/navigation/BottomNavItem.kt
package com.example.bookfusion.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItem(
        route = "home",
        label = "Home",
        icon = Icons.Filled.Home
    )

    data object Journal : BottomNavItem(
        route = "journal",
        label = "Journal",
        icon = Icons.Filled.Book
    )

    data object Moodboard : BottomNavItem(
        route = "moodboard",
        label = "Moodboard",
        icon = Icons.Filled.AutoAwesome
    )

    data object Settings : BottomNavItem(
        route = "settings",
        label = "Settings",
        icon = Icons.Filled.Settings
    )
}
