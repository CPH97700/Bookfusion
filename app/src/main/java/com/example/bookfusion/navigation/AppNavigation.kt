package com.example.bookfusion.navigation

import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bookapp.ui.screens.JournalScreen
import com.example.bookapp.viewmodel.BookViewModel
import com.example.bookfusion.screens.LoginScreen
import com.example.bookfusion.screens.SignupScreen
import com.example.bookfusion.ui.HomeScreen
import com.example.bookfusion.ui.screens.MoodBoardScreen
import com.example.bookfusion.ui.screens.StartScreen
import com.example.bookfusion.viewmodel.AuthViewModel
import com.example.bookfusion.ui.screens.MoodboardDetailScreen

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
    val bottomRoutes = setOf(
        BottomNavItem.Home.route,
        BottomNavItem.Journal.route,
        BottomNavItem.Moodboard.route
    )

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

        // Übersicht: Moodboard-Regal (NEU: Callback zum Öffnen eines Buch-Moodboards)
        composable(BottomNavItem.Moodboard.route) {
            MoodBoardScreen(
                onOpenMoodboard = { bookId, title ->
                    navController.navigate("moodboard/$bookId/${Uri.encode(title)}")
                }
            )
        }

        // Detail: Moodboard für ein ausgewähltes Buch (NEU)
        composable(
            route = "moodboard/{bookId}/{title}",
            arguments = listOf(
                navArgument("bookId") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId").orEmpty()
            val title = backStackEntry.arguments?.getString("title").orEmpty()
            MoodboardDetailScreen(
                bookId = bookId,
                title = title,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
