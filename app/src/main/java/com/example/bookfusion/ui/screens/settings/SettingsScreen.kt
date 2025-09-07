package com.example.bookfusion.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bookfusion.ui.screens.settings.components.AccountCard
import com.example.bookfusion.viewmodel.AuthViewModel

/**
 * **SettingsScreen**
 *
 * Der Einstellungs-Bereich der App.
 * Hier kann der User:
 *
 * - 🔙 Über die **TopBar** zurück navigieren
 * - 👤 Sein Konto sehen (E-Mail, UID, Status) via **AccountCard**
 * - 🚪 Sich abmelden (Logout-Button mit Icon)
 *
 * **UI-Design:**
 * - Hintergrund mit sanftem Farbverlauf (Lila → Hellblau)
 * - Oberfläche in **Material 3 Scaffold** eingebettet
 * - Inhalte in einer **Column** mit Abstand und Padding angeordnet
 *
 * @param navController Navigation, um zurück oder zur Start-Seite zu wechseln
 * @param authViewModel ViewModel, das den Login-/Logout-Zustand steuert
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val user by authViewModel.currentUser.collectAsState()

    val accent = Color(0xFF2A1A5E)
    val bgTop = Color(0xFFF7ECFF)
    val bgBottom = Color(0xFFEAF2FF)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Einstellungen") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück"
                        )
                    }
                }
            )
        }
    ) { inner ->
        Box(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(bgTop, bgBottom)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .systemBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                AccountCard(
                    email = user?.email,
                    uid = user?.uid,
                    loggedIn = user != null,
                    accent = accent
                )

                Button(
                    onClick = {
                        authViewModel.logout()
                        navController.popBackStack(route = "start", inclusive = false)
                    },
                    enabled = user != null,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accent,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Icon(Icons.Filled.ExitToApp, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Abmelden")
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }
}
