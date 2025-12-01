package com.example.nextbyte_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser = _currentUser

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        _isLoading.value = true
        viewModelScope.launch {
            // Firebase Auth mantiene la sesión automáticamente
            // Solo necesitamos verificar si hay un usuario logueado
            val user = auth.currentUser
            _isUserLoggedIn.value = user != null
            _currentUser.value = user
            _isLoading.value = false
        }
    }

    fun logout() {
        auth.signOut()
        _isUserLoggedIn.value = false
        _currentUser.value = null
    }

    fun getCurrentUserEmail(): String {
        return auth.currentUser?.email ?: ""
    }

    fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }
}
