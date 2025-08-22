// app/src/main/java/com/example/bookfusion/repository/FirebaseRepository.kt
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

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow(auth.currentUser)
    val authState: StateFlow<FirebaseUser?> = _authState

    private val _userBooks = MutableStateFlow<List<FirestoreBook>>(emptyList())
    val userBooks: StateFlow<List<FirestoreBook>> = _userBooks

    private val authListener = FirebaseAuth.AuthStateListener { a ->
        _authState.value = a.currentUser
        if (a.currentUser != null) {
            loadUserBooks()
        } else {
            _userBooks.value = emptyList()
        }
    }

    init {
        auth.addAuthStateListener(authListener)
        if (auth.currentUser != null) loadUserBooks()
    }

    fun clear() {
        runCatching { auth.removeAuthStateListener(authListener) }
    }



    fun register(email: String, password: String, onResult: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                val ok = task.isSuccessful
                if (!ok) {
                    Log.e("Auth", "❌ register failed: ${task.exception?.message}")
                } else {
                    Log.d("Auth", "✅ register ok")
                }
                onResult(ok)
            }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                val ok = task.isSuccessful
                if (!ok) {
                    Log.e("Auth", "❌ login failed: ${task.exception?.message}")
                } else {
                    Log.d("Auth", "✅ login ok")
                }
                onResult(ok)
            }
    }

    fun logout() {
        auth.signOut()
        _userBooks.value = emptyList()
        Log.d("Auth", "🚪 logout")
    }



    private fun col(): CollectionReference? {
        val uid = auth.currentUser?.uid ?: return null
        return db.collection("users").document(uid).collection("books")
    }

    fun saveBook(book: FirestoreBook, onResult: (Boolean) -> Unit) {
        val c = col() ?: return onResult(false)
        c.document(book.id)
            .set(book)
            .addOnSuccessListener {
                Log.d("FirestoreBooks", "✅ gespeichert: ${book.title}")
                loadUserBooks()
                onResult(true)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreBooks", "❌ Speichern fehlgeschlagen: ${e.message}")
                onResult(false)
            }
    }

    fun deleteBook(bookId: String, onResult: (Boolean) -> Unit) {
        val c = col() ?: return onResult(false)
        c.document(bookId)
            .delete()
            .addOnSuccessListener {
                Log.d("FirestoreBooks", "🗑 gelöscht: $bookId")
                loadUserBooks()
                onResult(true)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreBooks", "❌ Löschen fehlgeschlagen: ${e.message}")
                onResult(false)
            }
    }

    fun loadUserBooks() {
        val c = col() ?: return
        c.get()
            .addOnSuccessListener { snap ->
                _userBooks.value = snap.toObjects()
                Log.d("FirestoreBooks", "📚 geladen: ${_userBooks.value.size}")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreBooks", "❌ Laden fehlgeschlagen: ${e.message}")
            }
    }

    fun getBooks(onResult: (List<FirestoreBook>?) -> Unit) {
        val c = col() ?: return onResult(emptyList())
        c.get()
            .addOnSuccessListener { snap ->
                val books = snap.toObjects<FirestoreBook>()
                _userBooks.value = books
                onResult(books)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreBooks", "❌ getBooks fehlgeschlagen: ${e.message}")
                onResult(null)
            }
    }
}
