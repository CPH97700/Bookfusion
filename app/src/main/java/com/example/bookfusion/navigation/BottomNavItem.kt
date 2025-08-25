package com.example.bookfusion.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Beschreibt die einzelnen Tabs in der BottomNavigationBar.
 *
 * Jeder Eintrag hat:
 * - eine Route (zum Navigieren),
 * - ein Label (Text unten drunter),
 * - und ein Icon.
 */
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
