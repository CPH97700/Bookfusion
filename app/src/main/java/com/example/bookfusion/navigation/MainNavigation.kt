package com.example.bookfusion.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bookfusion.ui.screens.MoodboardScreen
import com.example.bookfusion.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookapp.ui.screens.JournalScreen
import com.example.bookapp.viewmodel.BookViewModel
import com.example.bookfusion.ui.HomeScreen

@Composable
fun MainNavigation(navController: NavHostController, padding: PaddingValues) {
    val authViewModel: AuthViewModel = viewModel()
    val bookViewModel = viewModel<BookViewModel>()

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route
    ) {
        composable(BottomNavItem.Home.route) {
            HomeScreen(navController, authViewModel,bookViewModel)
        }
        composable(BottomNavItem.Journal.route) {
            JournalScreen(bookViewModel)
        }
        composable(BottomNavItem.Moodboard.route) {
            MoodboardScreen()
        }
    }
}
