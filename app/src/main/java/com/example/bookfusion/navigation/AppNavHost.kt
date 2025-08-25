package com.example.bookfusion.navigation

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.bookapp.ui.screens.JournalScreen
import com.example.bookapp.viewmodel.BookViewModel
import com.example.bookfusion.screens.LoginScreen
import com.example.bookfusion.screens.SignupScreen
import com.example.bookfusion.ui.screens.home.HomeScreen
import com.example.bookfusion.ui.screens.loginSignup.StartScreen
import com.example.bookfusion.ui.screens.moodboard.MoodBoardScreen
import com.example.bookfusion.ui.screens.moodboard.MoodboardBookIntroScreen
import com.example.bookfusion.ui.screens.moodboard.MoodboardDetailScreen
import com.example.bookfusion.ui.screens.settings.SettingsScreen
import com.example.bookfusion.viewmodel.AuthViewModel
import com.example.bookfusion.viewmodel.MoodboardViewModel

/**
 * Haupt-Navigation der App.
 *
 * Hier definiere ich alle Screens (Login, Home, Journal, Moodboard, Settings)
 * und sage, wie ich von einem Screen zum nächsten komme.
 *
 * @param padding Abstand, den ich an den Inhalt weitergebe (z.B. wegen BottomNav)
 * @param navController steuert die Navigation
 * @param authViewModel hält Infos zum aktuellen Nutzer (Login/Logout)
 * @param bookViewModel hält Infos zu den Büchern
 */
@Composable
fun AppNavHost(
    padding: PaddingValues,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    bookViewModel: BookViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val startDestination = if (currentUser == null) "start" else BottomNavItem.Home.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable("start") { StartScreen(navController) }
        composable("login") { LoginScreen(navController, authViewModel) }
        composable("signup") { SignupScreen(navController, authViewModel) }

        composable(BottomNavItem.Home.route) {
            HomeScreen(navController, authViewModel, bookViewModel)
        }

        composable(BottomNavItem.Journal.route) {
            val moodboardVM: MoodboardViewModel = viewModel()
            val context = LocalContext.current

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

                    Toast.makeText(
                        context,
                        "Zum Moodboard hinzugefügt",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

        composable(BottomNavItem.Moodboard.route) {
            val moodboardVM: MoodboardViewModel = viewModel()

            MoodBoardScreen(
                entriesFlow = moodboardVM.entries,
                onOpenMoodboard = { bookId, title ->
                    navController.navigate("moodboard/intro/$bookId/${Uri.encode(title)}")
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Moodboard Intro
        composable(
            route = "moodboard/intro/{bookId}/{title}",
            arguments = listOf(
                navArgument("bookId") { type = NavType.StringType },
                navArgument("title")  { type = NavType.StringType }
            )
        ) { back ->
            val moodboardVM: MoodboardViewModel = viewModel()

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
                    navController.navigate("moodboard/$bookId/${Uri.encode(title)}")
                },
                moodboardVM = moodboardVM
            )
        }

        // Moodboard Detail / Builder
        composable(
            route = "moodboard/{bookId}/{title}",
            arguments = listOf(
                navArgument("bookId") { type = NavType.StringType },
                navArgument("title")  { type = NavType.StringType }
            )
        ) { back ->
            val moodboardVM: MoodboardViewModel = viewModel()

            val bookId = back.arguments?.getString("bookId").orEmpty()
            val title  = back.arguments?.getString("title").orEmpty()

            MoodboardDetailScreen(
                bookId = bookId,
                title  = title,
                onBack = { navController.popBackStack() },
                moodboardVM = moodboardVM
            )
        }

        // Settings
        composable("settings") {
            SettingsScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}
