package com.example.bookfusion.ui.screens.settings.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * **AccountCard**
 *
 * Eine Karte, die die **Account-Informationen des Nutzers** anzeigt.
 *
 * Features:
 * - 👤 Zeigt ein Avatar-Kreis mit dem ersten Buchstaben der E-Mail
 * - 📧 Anzeige der hinterlegten E-Mail
 * - 🔑 Anzeige der Firebase-UID
 * - ✅ Statusanzeige (angemeldet / abgemeldet)
 * - 🎨 Design: Abgerundete Karte mit Accent-Farbe
 *
 * @param email Die E-Mail-Adresse des Nutzers (oder `null`, wenn nicht eingeloggt)
 * @param uid Die Firebase-UID des Nutzers (oder `null`)
 * @param loggedIn Zeigt an, ob der Nutzer eingeloggt ist
 * @param accent Akzentfarbe für Text & Avatar
 */
@Composable
fun AccountCard(
    email: String?,
    uid: String?,
    loggedIn: Boolean,
    accent: Color
) {
    Text(
        "Konto",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = accent
    )

    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFFFF7FE)),
        elevation = CardDefaults.elevatedCardElevation(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val initial = (email?.firstOrNull()?.uppercaseChar() ?: '•').toString()
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(initial, color = accent, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        text = email ?: "Nicht angemeldet",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = accent
                    )
                    val statusText = if (loggedIn) "angemeldet" else "abgemeldet"
                    AssistChip(onClick = {}, label = { Text("Status: $statusText") })
                }
            }

            SettingsRow(
                icon = { Icon(Icons.Default.Email, contentDescription = null) },
                label = "E-Mail",
                value = email ?: "—"
            )

            SettingsRow(
                icon = { Icon(Icons.Default.Fingerprint, contentDescription = null) },
                label = "UID",
                value = uid ?: "—"
            )
        }
    }
}
