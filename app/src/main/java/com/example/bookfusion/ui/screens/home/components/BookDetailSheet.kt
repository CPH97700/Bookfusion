package com.example.bookfusion.ui.screens.home.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bookapp.model.BookItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import androidx.compose.foundation.layout.Arrangement

private const val TAG = "BookDetailSheet"

/**
 * Ein Bottom-Sheet mit Buchdetails.
 *
 * - Holt zusätzliche Infos aus der Google Books API.
 * - Zeigt Cover, Titel, Autoren, Seitenzahl, Verlag, Bewertung usw.
 * - Bietet Buttons für Kauf oder Vorschau.
 *
 * @param volumeId Die Google Books ID des Buchs
 * @param visible Ob das Sheet sichtbar ist
 * @param onDismiss Callback beim Schließen
 * @param fallback Optional: Buchdaten, falls API nichts liefert
 * @param accent Akzentfarbe für Überschriften
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailSheet(
    volumeId: String,
    visible: Boolean,
    onDismiss: () -> Unit,
    fallback: BookItem? = null,
    accent: Color = Color(0xFF2A1A5E)
) {
    val uri = LocalUriHandler.current

    var details by remember(volumeId) { mutableStateOf<BookDetails?>(null) }
    var loading by remember(volumeId) { mutableStateOf(true) }
    var error by remember(volumeId) { mutableStateOf<String?>(null) }

    LaunchedEffect(volumeId, visible) {
        if (!visible) return@LaunchedEffect
        loading = true
        error = null
        details = try {
            GoogleBooksApi.fetchVolumeDetails(volumeId)
        } catch (t: Throwable) {
            Log.e(TAG, "fetchVolumeDetails failed", t)
            error = t.message ?: "Unbekannter Fehler"
            null
        }
        loading = false
    }

    if (!visible) return

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Cover + Titel + Autoren + Meta
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val cover =
                    details?.thumbnail
                        ?: fallback?.volumeInfo?.imageLinks?.thumbnail?.replace("http://", "https://")

                AsyncImage(
                    model = cover,
                    contentDescription = details?.title ?: fallback?.volumeInfo?.title,
                    modifier = Modifier
                        .size(width = 110.dp, height = 150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEAE6FF))
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        details?.title ?: (fallback?.volumeInfo?.title ?: ""),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = accent
                    )

                    val authors = details?.authors ?: fallback?.volumeInfo?.authors
                    if (!authors.isNullOrEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            authors.joinToString(", "),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Meta nur aus API-Details
                    val metaBits = mutableListOf<String>().apply {
                        details?.pageCount?.let { add("$it Seiten") }
                        details?.publishedDate?.let { add(it) }
                        details?.publisher?.let { add(it) }
                    }
                    if (metaBits.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            metaBits.joinToString(" • "),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val buy = details?.buyLink
                if (!buy.isNullOrBlank()) {
                    Button(onClick = { uri.openUri(buy) }, modifier = Modifier.weight(1f)) {
                        Text("Kaufen")
                    }
                }
                val preview = details?.previewLink ?: details?.infoLink
                if (!preview.isNullOrBlank()) {
                    OutlinedButton(onClick = { uri.openUri(preview) }, modifier = Modifier.weight(1f)) {
                        Text("Vorschau")
                    }
                }
            }

            if (loading) {
                Spacer(Modifier.height(16.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Text("Lade Details von Google Books …", style = MaterialTheme.typography.bodySmall)
            } else if (error != null) {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Konnte Details nicht laden: $error",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            val desc = (details?.description ?: fallback?.volumeInfo?.description).orEmpty().let(::stripHtml)
            if (desc.isNotBlank()) {
                Spacer(Modifier.height(16.dp))
                Text("Beschreibung", style = MaterialTheme.typography.titleMedium, color = accent)
                Spacer(Modifier.height(6.dp))

                var expanded by remember { mutableStateOf(false) }
                val maxLines = if (expanded) Int.MAX_VALUE else 7

                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = maxLines,
                    overflow = TextOverflow.Ellipsis
                )

                if (desc.length > 240) {
                    TextButton(onClick = { expanded = !expanded }) {
                        Text(if (expanded) "Weniger anzeigen" else "Mehr anzeigen")
                    }
                }
            }

            val categories: List<String>? = details?.categories
            val rating = details?.averageRating
            val ratingsCount = details?.ratingsCount

            if (!categories.isNullOrEmpty() || rating != null) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories?.forEach { AssistChip(onClick = {}, label = { Text(it) }) }
                    if (rating != null) {
                        AssistChip(
                            onClick = {},
                            label = { Text(String.format("%.1f ★ (%d)", rating, ratingsCount ?: 0)) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}


private fun stripHtml(html: String): String {
    return html
        .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
        .replace(Regex("<.*?>"), "")
        .trim()
}

/** Datenmodell fürs Sheet */
data class BookDetails(
    val title: String?,
    val authors: List<String>?,
    val description: String?,
    val pageCount: Int?,
    val publisher: String?,
    val publishedDate: String?,
    val categories: List<String>?,
    val averageRating: Double?,
    val ratingsCount: Int?,
    val previewLink: String?,
    val infoLink: String?,
    val buyLink: String?,
    val thumbnail: String?
)

/** Google-Books-Fetch ohne Extra-Dependencies */
object GoogleBooksApi {
    suspend fun fetchVolumeDetails(volumeId: String): BookDetails? = withContext(Dispatchers.IO) {
        val safeId = URLEncoder.encode(volumeId, "UTF-8")
        val url = URL("https://www.googleapis.com/books/v1/volumes/$safeId")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 10_000
        }

        conn.inputStream.bufferedReader().use { reader ->
            val json = JSONObject(reader.readText())
            val volumeInfo = json.optJSONObject("volumeInfo") ?: JSONObject()
            val saleInfo = json.optJSONObject("saleInfo") ?: JSONObject()

            fun toList(arr: JSONArray?): List<String>? =
                arr?.let { (0 until it.length()).map { idx -> it.optString(idx) } }?.filter { it.isNotBlank() }

            val imageLinks = volumeInfo.optJSONObject("imageLinks")
            val thumb = imageLinks?.optString("thumbnail")?.replace("http://", "https://")

            return@use BookDetails(
                title = volumeInfo.optString("title").takeIf { it.isNotBlank() },
                authors = toList(volumeInfo.optJSONArray("authors")),
                description = volumeInfo.optString("description").takeIf { it.isNotBlank() },
                pageCount = volumeInfo.optInt("pageCount").takeIf { it > 0 },
                publisher = volumeInfo.optString("publisher").takeIf { it.isNotBlank() },
                publishedDate = volumeInfo.optString("publishedDate").takeIf { it.isNotBlank() },
                categories = toList(volumeInfo.optJSONArray("categories")),
                averageRating = volumeInfo.optDouble("averageRating", Double.NaN).let { if (it.isNaN()) null else it },
                ratingsCount = volumeInfo.optInt("ratingsCount").takeIf { it > 0 },
                previewLink = volumeInfo.optString("previewLink").takeIf { it.isNotBlank() },
                infoLink = volumeInfo.optString("infoLink").takeIf { it.isNotBlank() },
                buyLink = saleInfo.optString("buyLink").takeIf { it.isNotBlank() },
                thumbnail = thumb
            )
        }
    }
}
