package com.example.bookfusion.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.bookfusion.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow

/**
 * **AuthViewModel**
 *
 * ViewModel für die **Authentifizierung** mit Firebase.
 * Es stellt die zentrale Schnittstelle zwischen UI und [FirebaseRepository] dar.
 *
 * **Funktionen:**
 * - 👤 `currentUser`: Reaktiver StateFlow des aktuellen [FirebaseUser] (null, wenn nicht eingeloggt)
 * - ✍️ `register(email, password)`: Registriert einen neuen User in Firebase Auth
 * - 🔑 `login(email, password)`: Meldet einen existierenden User an
 * - 🚪 `logout()`: Meldet den aktuellen User ab und leert die States
 *
 * **Architektur:**
 * - UI → ruft Methoden im `AuthViewModel` auf
 * - `AuthViewModel` → delegiert Logik an [FirebaseRepository]
 * - Repository → kümmert sich um direkte Firebase-Kommunikation
 *
 * @param application Kontext der App (notwendig für [AndroidViewModel])
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FirebaseRepository()

    val currentUser: StateFlow<FirebaseUser?> = repository.authState

    fun register(email: String, password: String, onResult: (Boolean) -> Unit) {
        repository.register(email, password, onResult)
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        repository.login(email, password, onResult)
    }


    fun logout() {
        repository.logout()
    }
}