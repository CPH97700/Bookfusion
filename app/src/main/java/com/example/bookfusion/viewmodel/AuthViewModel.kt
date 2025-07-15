package com.example.bookfusion.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.bookfusion.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow


//📂 ViewModel zu verwaltung von Login/Registrierung & User-Status
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    //🔄Zugriff auf das Repository, das die Authentiefizierungslogik enthält
    private val repository = FirebaseRepository()

    //👤Aktueller eingeloggte Benutzer als StateFlow
    val currentUser: StateFlow<FirebaseUser?> = repository.authState

    //🧾 Registrierung eines neuen Users mit E-Mail & Passwort
    fun register(email: String, password: String, onResult: (Boolean) -> Unit) {
        repository.register(email, password, onResult)
    }

    //🔓login mit E-Mail & Passwort
    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        repository.login(email, password, onResult)
    }


    //🚪Benutzer Abmelden
    fun logout() {
        repository.logout()
    }
}