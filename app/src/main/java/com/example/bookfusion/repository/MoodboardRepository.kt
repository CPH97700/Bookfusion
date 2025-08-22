package com.example.bookfusion.repository

import com.example.bookfusion.model.FirestoreMoodboardEntry
import com.example.bookfusion.model.FirestoreUnsplashPhoto
import com.example.bookfusion.model.MoodboardEntry
import com.example.bookfusion.model.toMoodboardEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MoodboardRepository {

    private val auth get() = FirebaseAuth.getInstance()
    private val db get() = FirebaseFirestore.getInstance()

    private fun col() = db.collection("users")
        .document(requireNotNull(auth.currentUser?.uid) { "User not logged in" })
        .collection("moodboards")

    fun listenMoodboards(): Flow<List<MoodboardEntry>> = callbackFlow {
        val reg = col().addSnapshotListener { snap, err ->
            if (err != null) {
                trySend(emptyList()); return@addSnapshotListener
            }
            val fs = snap?.toObjects<FirestoreMoodboardEntry>().orEmpty()
            trySend(fs.map { it.toMoodboardEntry() })
        }
        awaitClose { reg.remove() }
    }

    suspend fun ensureEntry(bookId: String, title: String, coverUrl: String) {
        val ref = col().document(bookId)
        val existing = ref.get().await().toObject(FirestoreMoodboardEntry::class.java)
        val next = (existing ?: FirestoreMoodboardEntry(bookId, title, coverUrl))
            .copy(title = title, coverUrl = coverUrl)
        ref.set(next).await()
    }

    suspend fun addPhoto(bookId: String, photo: FirestoreUnsplashPhoto) {
        val ref = col().document(bookId)
        val doc = ref.get().await().toObject(FirestoreMoodboardEntry::class.java) ?: return
        if (doc.photos.any { it.id == photo.id }) return
        ref.update("photos", doc.photos + photo).await()
    }

    suspend fun removePhoto(bookId: String, photoId: String) {
        val ref = col().document(bookId)
        val doc = ref.get().await().toObject(FirestoreMoodboardEntry::class.java) ?: return
        ref.update("photos", doc.photos.filterNot { it.id == photoId }).await()
    }

    suspend fun deleteEntry(bookId: String) {
        col().document(bookId).delete().await()
    }
}
