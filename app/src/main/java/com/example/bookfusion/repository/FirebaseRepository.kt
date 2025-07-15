package com.example.bookfusion.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FirebaseRepository {

    //🔐 Zugriff auf Firebase Authentication
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // 📡 StateFlow zur Beobachtung des aktuell eingeloggten Users
    private val _authState = MutableStateFlow(firebaseAuth.currentUser)
    val authState: StateFlow<FirebaseUser?> = _authState

    // 👂 AuthStateListener erkennt Login-/Logout-Änderungen
    private val authListener = FirebaseAuth.AuthStateListener { auth ->
        _authState.value = auth.currentUser
    }

    init {
        firebaseAuth.addAuthStateListener(authListener)
    }

    // 🧾 Registrierung eines neuen Users mit E-Mail & Passwort
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

    // 🔓 Login mit E-Mail & Passwort
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

    // 🚪 Logout-Funktion → User wird abgemeldet
    fun logout() {
        firebaseAuth.signOut()
        Log.d("🔥 FirebaseAuth", "🚪 User abgemeldet")
    }
}
