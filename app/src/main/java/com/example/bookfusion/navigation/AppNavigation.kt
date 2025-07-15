package com.example.bookfusion.navigation
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookfusion.screens.LoginScreen
import com.example.bookfusion.screens.SignupScreen
import com.example.bookfusion.ui.screens.HomeScreen
import com.example.bookfusion.ui.screens.StartScreen
import com.example.bookfusion.viewmodel.AuthViewModel

// 🧭 Navigation mit StartScreen → Login → Signup → Home

@Composable
fun AppNavigation(viewModel: AuthViewModel) {
    val navController = rememberNavController()
    val currentUser by viewModel.currentUser.collectAsState()

    NavHost(navController = navController, startDestination = "start") {
        composable("start") { StartScreen(navController) }
        composable("login") { LoginScreen(navController, viewModel) }
        composable("signup") { SignupScreen(navController, viewModel) }
        composable("home") { HomeScreen(navController, viewModel) }
    }

    // 🔁 automatische Weiterleitung wenn eingeloggt
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }
}
