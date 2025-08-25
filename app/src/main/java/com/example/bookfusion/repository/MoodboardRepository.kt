package com.example.bookfusion.repository

import com.example.bookfusion.model.FirestoreMoodboardEntry
import com.example.bookfusion.model.FirestoreUnsplashPhoto
import com.example.bookfusion.model.MoodboardEntry
import com.example.bookfusion.model.toMoodboardEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
/**
 * Repository für die Moodboards in Firestore.
 *
 * Hier speichere ich für jedes Buch Bilder (von Unsplash),
 * die zusammen ein Moodboard ergeben.
 *
 * - Moodboards anhören (Live-Updates)
 * - Moodboard-Eintrag anlegen/löschen
 * - Fotos hinzufügen oder entfernen
 */
class MoodboardRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun colOrNull(): CollectionReference? =
        auth.currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).collection("moodboards")
        }

    fun listenMoodboards(): Flow<List<MoodboardEntry>> = callbackFlow {
        val ref = colOrNull()
        if (ref == null) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }

        val reg = ref.addSnapshotListener { snap, err ->
            if (err != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val fs = snap?.toObjects<FirestoreMoodboardEntry>().orEmpty()
            trySend(fs.map { it.toMoodboardEntry() })
        }
        awaitClose { reg.remove() }
    }

    suspend fun ensureEntry(bookId: String, title: String, coverUrl: String) {
        val ref = colOrNull()?.document(bookId) ?: return
        val existing = ref.get().await().toObject(FirestoreMoodboardEntry::class.java)
        val next = (existing ?: FirestoreMoodboardEntry(bookId, title, coverUrl))
            .copy(title = title, coverUrl = coverUrl)
        ref.set(next).await()
    }

    suspend fun addPhoto(bookId: String, photo: FirestoreUnsplashPhoto) {
        val ref = colOrNull()?.document(bookId) ?: return
        val doc = ref.get().await().toObject(FirestoreMoodboardEntry::class.java) ?: return
        if (doc.photos.any { it.id == photo.id }) return
        ref.update("photos", doc.photos + photo).await()
    }

    suspend fun removePhoto(bookId: String, photoId: String) {
        val ref = colOrNull()?.document(bookId) ?: return
        val doc = ref.get().await().toObject(FirestoreMoodboardEntry::class.java) ?: return
        ref.update("photos", doc.photos.filterNot { it.id == photoId }).await()
    }

    suspend fun deleteEntry(bookId: String) {
        colOrNull()?.document(bookId)?.delete()?.await()
    }
}
