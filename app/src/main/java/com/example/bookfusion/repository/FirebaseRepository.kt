package com.example.bookfusion.repository

import android.util.Log
import com.example.bookfusion.model.FirestoreBook
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FirebaseRepository {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow(firebaseAuth.currentUser)
    val authState: StateFlow<FirebaseUser?> = _authState

    // 📚 State für persistente Bücher
    private val _userBooks = MutableStateFlow<List<FirestoreBook>>(emptyList())
    val userBooks: StateFlow<List<FirestoreBook>> = _userBooks

    private val authListener = FirebaseAuth.AuthStateListener { auth ->
        _authState.value = auth.currentUser
        if (auth.currentUser != null) {
            loadUserBooks() // 🔄 Bücher direkt laden, wenn User eingeloggt
        } else {
            _userBooks.value = emptyList()
        }
    }

    init {
        firebaseAuth.addAuthStateListener(authListener)
        if (firebaseAuth.currentUser != null) {
            loadUserBooks()
        }
    }

    fun clear() {
        try {
            firebaseAuth.removeAuthStateListener(authListener)
        } catch (_: Exception) {
            // Ignorieren – Listener evtl. schon entfernt
        }
    }

    fun register(email: String, password: String, onResult: (Boolean) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("🔥 FirebaseAuth", "✅ Registrierung erfolgreich")
                    onResult(true)
                } else {
                    Log.e("🔥 FirebaseAuth", "❌ Registrierung fehlgeschlagen: ${task.exception?.message}")
                    onResult(false)
                }
            }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("🔥 FirebaseAuth", "✅ Login erfolgreich")
                    onResult(true)
                } else {
                    Log.e("🔥 FirebaseAuth", "❌ Login fehlgeschlagen: ${task.exception?.message}")
                    onResult(false)
                }
            }
    }

    fun logout() {
        firebaseAuth.signOut()
        Log.d("🔥 FirebaseAuth", "🚪 User abgemeldet")
    }

    private fun userBooksCollection(): CollectionReference? {
        val uid = firebaseAuth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            Log.w("🔥 Firestore", "⚠️ Kein eingeloggter User – Bücher-Sammlung nicht verfügbar")
            return null
        }
        return firestore.collection("users").document(uid).collection("books")
    }

    // 💾 Buch speichern/aktualisieren
    fun saveBook(book: FirestoreBook, onResult: (Boolean) -> Unit) {
        val col = userBooksCollection() ?: return onResult(false)

        col.document(book.id)
            .set(book)
            .addOnSuccessListener {
                Log.d("🔥 Firestore", "✅ Buch gespeichert: ${book.title}")
                loadUserBooks() // 🔄 State updaten
                onResult(true)
            }
            .addOnFailureListener { e ->
                Log.e("🔥 Firestore", "❌ Fehler beim Speichern: ${e.message}")
                onResult(false)
            }
    }

    // 📥 Bücher aus Firestore holen und im State speichern
    fun loadUserBooks() {
        val col = userBooksCollection() ?: return
        col.get()
            .addOnSuccessListener { snapshot ->
                val books = snapshot.toObjects<FirestoreBook>()
                _userBooks.value = books
                Log.d("🔥 Firestore", "📚 ${books.size} Bücher geladen")
            }
            .addOnFailureListener { e ->
                Log.e("🔥 Firestore", "❌ Fehler beim Laden: ${e.message}")
            }
    }

    fun getBooks(onResult: (List<FirestoreBook>?) -> Unit) {
        val col = userBooksCollection() ?: return onResult(emptyList())

        col.get()
            .addOnSuccessListener { snapshot ->
                val books = snapshot.toObjects<FirestoreBook>()
                _userBooks.value = books
                Log.d("🔥 Firestore", "📚 ${books.size} Bücher geladen")
                onResult(books)
            }
            .addOnFailureListener { e ->
                Log.e("🔥 Firestore", "❌ Fehler beim Laden: ${e.message}")
                onResult(null)
            }
    }

    fun deleteBook(bookId: String, onResult: (Boolean) -> Unit) {
        val col = userBooksCollection() ?: return onResult(false)

        col.document(bookId)
            .delete()
            .addOnSuccessListener {
                Log.d("🔥 Firestore", "🗑 Buch gelöscht: $bookId")
                loadUserBooks() // 🔄 State updaten
                onResult(true)
            }
            .addOnFailureListener { e ->
                Log.e("🔥 Firestore", "❌ Fehler beim Löschen: ${e.message}")
                onResult(false)
            }
    }
}
