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
import androidx.navigation.NavHostController
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
import com.example.bookfusion.ui.screens.MoodboardBookIntroScreen
import com.example.bookfusion.ui.screens.MoodboardDetailScreen
import com.example.bookfusion.ui.screens.SettingsScreen
import com.example.bookfusion.ui.screens.StartScreen
import com.example.bookfusion.viewmodel.AuthViewModel
import com.example.bookfusion.viewmodel.MoodboardViewModel

@Composable
fun AppNavigation(authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    val bookViewModel = viewModel<BookViewModel>()
    val moodboardVM  = viewModel<MoodboardViewModel>()

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
    ) { padding ->
        AppNavHost(
            padding = padding,
            navController = navController,
            authViewModel = authViewModel,
            bookViewModel = bookViewModel,
            moodboardVM  = moodboardVM
        )
    }
}

@Composable
private fun AppNavHost(
    padding: PaddingValues,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    bookViewModel: BookViewModel,
    moodboardVM: MoodboardViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val startDestination = if (currentUser == null) "start" else BottomNavItem.Home.route

    NavHost(navController = navController, startDestination = startDestination) {
        // Auth
        composable("start") { StartScreen(navController) }
        composable("login") { LoginScreen(navController, authViewModel) }
        composable("signup") { SignupScreen(navController, authViewModel) }

        composable(BottomNavItem.Home.route) {
            HomeScreen(navController, authViewModel, bookViewModel)
        }

        composable(BottomNavItem.Journal.route) {
            JournalScreen(
                viewModel = bookViewModel,
                onOpenMoodboard = { bookId, title ->
                    val coverUrl = run {
                        val liked = bookViewModel.likedBooks.firstOrNull { it.id == bookId }
                        val read  = bookViewModel.readBooks.firstOrNull { it.id == bookId }
                        val item = liked ?: read
                        item?.volumeInfo?.imageLinks?.thumbnail
                            ?.replace("http://", "https://")
                            .orEmpty()
                    }
                    moodboardVM.addBookIfMissing(bookId, title, coverUrl)
                    navController.navigate(BottomNavItem.Moodboard.route)
                }
            )
        }


        composable(BottomNavItem.Moodboard.route) {
            MoodBoardScreen(
                entriesFlow = moodboardVM.entries,
                onOpenMoodboard = { bookId, title ->
                    navController.navigate("moodboard/intro/$bookId/${Uri.encode(title)}")
                }
            )
        }

        composable(
            route = "moodboard/intro/{bookId}/{title}",
            arguments = listOf(
                navArgument("bookId") { type = NavType.StringType },
                navArgument("title")  { type = NavType.StringType }
            )
        ) { back ->
            val bookId = back.arguments?.getString("bookId").orEmpty()
            val title  = back.arguments?.getString("title").orEmpty()

            val entry = moodboardVM.entries.collectAsState().value.firstOrNull { it.bookId == bookId }
            val coverUrl = entry?.coverUrl ?: run {
                val liked = bookViewModel.likedBooks.firstOrNull { it.id == bookId }
                val read  = bookViewModel.readBooks.firstOrNull { it.id == bookId }
                val item = liked ?: read
                item?.volumeInfo?.imageLinks?.thumbnail?.replace("http://", "https://").orEmpty()
            }

            MoodboardBookIntroScreen(
                bookId = bookId,
                title = title,
                coverUrl = coverUrl,
                onBack = { navController.popBackStack() },
                onAddClick = {
                    // jump into builder (search & add photos)
                    navController.navigate("moodboard/$bookId/${Uri.encode(title)}")
                },
                moodboardVM = moodboardVM
            )
        }

        composable(
            route = "moodboard/{bookId}/{title}",
            arguments = listOf(
                navArgument("bookId") { type = NavType.StringType },
                navArgument("title")  { type = NavType.StringType }
            )
        ) { back ->
            val bookId = back.arguments?.getString("bookId").orEmpty()
            val title  = back.arguments?.getString("title").orEmpty()

            MoodboardDetailScreen(
                bookId = bookId,
                title  = title,
                onBack = { navController.popBackStack() },
                moodboardVM = moodboardVM
            )
        }
        composable("settings") {
            SettingsScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}


