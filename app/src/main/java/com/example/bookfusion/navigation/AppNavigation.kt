package com.example.bookfusion.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bookapp.ui.screens.JournalScreen
import com.example.bookapp.viewmodel.BookViewModel
import com.example.bookfusion.screens.LoginScreen
import com.example.bookfusion.screens.SignupScreen
import com.example.bookfusion.ui.HomeScreen
import com.example.bookfusion.ui.screens.MoodBoardScreen
import com.example.bookfusion.ui.screens.StartScreen
import com.example.bookfusion.viewmodel.AuthViewModel

@Composable
fun AppNavigation(authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    // ViewModels, die in mehreren Screens verwendet werden
    val bookViewModel = viewModel<BookViewModel>()

    // Login-State
    val currentUser by authViewModel.currentUser.collectAsState()

    // Aktuelle Route beobachten (für BottomBar-Visibility & Selection)
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = backStackEntry.value?.destination

    // Welche Routen gehören zur Bottom Navigation?
    val bottomRoutes = remember {
        setOf(
            BottomNavItem.Home.route,
            BottomNavItem.Journal.route,
            BottomNavItem.Moodboard.route
        )
    }

    // BottomBar nur zeigen, wenn User eingeloggt UND auf Bottom-Route
    val showBottomBar = (currentUser != null) &&
            currentDestination?.hierarchy?.any { it.route in bottomRoutes } == true

    // Wenn sich der Login-Status ändert, auf die passende Route navigieren (Stack clean)
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
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    navController = navController,
                )
            }
        }
    ) { padding ->
        AppNavHost(
            padding = padding,
            navController = navController,
            authViewModel = authViewModel,
            bookViewModel = bookViewModel
        )
    }
}

@Composable
private fun AppNavHost(
    padding: PaddingValues,
    navController: androidx.navigation.NavHostController,
    authViewModel: AuthViewModel,
    bookViewModel: BookViewModel
) {
    // Startziel abhängig vom Login-State: wird zusätzlich durch LaunchedEffect gesichert
    val currentUser by authViewModel.currentUser.collectAsState()
    val startDestination = if (currentUser == null) "start" else BottomNavItem.Home.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth / Onboarding
        composable("start") { StartScreen(navController) }
        composable("login") { LoginScreen(navController, authViewModel) }
        composable("signup") { SignupScreen(navController, authViewModel) }

        // Bottom-Navigation Ziele
        composable(BottomNavItem.Home.route) {
            HomeScreen(navController, authViewModel, bookViewModel)
        }
        composable(BottomNavItem.Journal.route) {
            JournalScreen(bookViewModel)
        }
        composable(BottomNavItem.Moodboard.route) {
            MoodBoardScreen()
        }


    }
}
