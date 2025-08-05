package com.example.bookfusion.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bookfusion.ui.screens.JournalScreen
import com.example.bookfusion.ui.screens.MoodboardScreen
import com.example.bookfusion.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookfusion.ui.HomeScreen

@Composable
fun MainNavigation(navController: NavHostController, padding: PaddingValues) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route
    ) {
        composable(BottomNavItem.Home.route) {
            HomeScreen(navController, authViewModel)
        }
        composable(BottomNavItem.Journal.route) {
            JournalScreen()
        }
        composable(BottomNavItem.Moodboard.route) {
            MoodboardScreen()
        }
    }
}
