package com.example.bookfusion.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.bookfusion.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow


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