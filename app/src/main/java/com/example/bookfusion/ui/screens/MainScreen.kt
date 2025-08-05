package com.example.bookfusion.ui.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.bookfusion.navigation.BottomNavigationBar
import com.example.bookfusion.navigation.MainNavigation

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) {
        MainNavigation(navController = navController, padding = it)
    }
}
