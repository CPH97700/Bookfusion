package com.example.bookfusion.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bookapp.viewmodel.BookViewModel
import com.example.bookfusion.viewmodel.AuthViewModel

/**
 * Steuert die komplette Navigation der App.
 *
 * Hier wird entschieden:
 * - ob der Nutzer eingeloggt ist oder nicht
 * - ob die BottomNavigationBar angezeigt wird
 * - und welches der Start-Screen ist (Start oder Home).
 */

@Composable
fun AppNavigation(authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    val bookViewModel = viewModel<BookViewModel>()

    val currentUser by authViewModel.currentUser.collectAsState()

    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = backStackEntry.value?.destination

    val bottomRoutes = setOf(
        BottomNavItem.Home.route,
        BottomNavItem.Journal.route,
        BottomNavItem.Moodboard.route
    )

    val showBottomBar = (currentUser != null) &&
            currentDestination?.hierarchy?.any { it.route in bottomRoutes } == true

    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            navController.navigate("start") {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        } else {
            navController.navigate(BottomNavItem.Home.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        bottomBar = { if (showBottomBar) BottomNavigationBar(navController = navController) }
    ) { padding: PaddingValues ->
        AppNavHost(
            padding = padding,
            navController = navController,
            authViewModel = authViewModel,
            bookViewModel = bookViewModel
        )
    }
}
