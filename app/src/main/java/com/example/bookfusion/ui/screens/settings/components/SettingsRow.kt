package com.example.bookfusion.ui.screens.settings.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * **SettingsRow**
 *
 * Eine einzelne Zeile in den Einstellungen, die ein **Icon, ein Label und einen Wert** darstellt.
 *
 * Features:
 * - 🔘 Kreisförmiger Icon-Hintergrund für klare visuelle Struktur
 * - 🏷 Label (kleiner Text, erklärt, was angezeigt wird)
 * - 📄 Wert (z. B. E-Mail oder UID)
 * - 📦 Eingepackt in eine Surface mit leichter Elevation & abgerundeten Ecken
 *
 * Typischer Einsatz: Unter **AccountCard** oder anderen Einstellungs-Karten.
 *
 * @param icon Das führende Symbol in der Zeile (Composable-Icon)
 * @param label Beschriftung der Einstellung (z. B. "E-Mail")
 * @param value Der Wert, der zum Label angezeigt wird (z. B. die Adresse)
 */
@Composable
fun SettingsRow(
    icon: @Composable () -> Unit,
    label: String,
    value: String
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEDE7FF)),
                contentAlignment = Alignment.Center
            ) { icon() }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
