package com.example.bookfusion.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookapp.ui.screens.JournalScreen
import com.example.bookapp.viewmodel.BookViewModel
import com.example.bookfusion.screens.LoginScreen
import com.example.bookfusion.screens.SignupScreen
import com.example.bookfusion.ui.HomeScreen
import com.example.bookfusion.ui.screens.StartScreen
import com.example.bookfusion.ui.screens.MainScreen
import com.example.bookfusion.viewmodel.AuthViewModel


@Composable
fun AppNavigation(viewModel: AuthViewModel) {
    val navController = rememberNavController()
    val currentUser by viewModel.currentUser.collectAsState()
    val bookViewModel = viewModel<BookViewModel>()

    NavHost(navController = navController, startDestination = "start") {
        composable("start") { StartScreen(navController) }
        composable("login") { LoginScreen(navController, viewModel) }
        composable("signup") { SignupScreen(navController, viewModel) }
        composable("home") { HomeScreen(navController, viewModel,bookViewModel) }
        composable("main") { MainScreen() }
        composable("journal") { JournalScreen(bookViewModel) }
    }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navController.navigate("main") {
                popUpTo("login") { inclusive = true }
            }
        }
    }
}
