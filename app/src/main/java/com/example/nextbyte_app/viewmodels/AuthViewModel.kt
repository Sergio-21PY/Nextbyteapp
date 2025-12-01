package com.example.nextbyte_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextbyte_app.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val repository = FirebaseRepository()

    private val _isUserLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<com.google.firebase.auth.FirebaseUser?> = _currentUser

    // Para comunicar resultados a la UI
    private val _updateResult = MutableStateFlow<UpdateResult?>(null)
    val updateResult: StateFlow<UpdateResult?> = _updateResult

    sealed class UpdateResult {
        object Success : UpdateResult()
        data class Error(val message: String) : UpdateResult()
    }

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        _isLoading.value = true
        _currentUser.value = auth.currentUser
        _isUserLoggedIn.value = auth.currentUser != null
        _isLoading.value = false
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
        _isUserLoggedIn.value = false
    }

    fun changeEmail(password: String, newEmail: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val reauthSuccess = repository.reauthenticateUser(password)
            if (reauthSuccess) {
                val changeSuccess = repository.changeUserEmail(newEmail)
                if (changeSuccess) {
                    _updateResult.value = UpdateResult.Success
                } else {
                    _updateResult.value = UpdateResult.Error("No se pudo cambiar el correo en la base de datos.")
                }
            } else {
                _updateResult.value = UpdateResult.Error("La contraseña es incorrecta.")
            }
            _isLoading.value = false
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val reauthSuccess = repository.reauthenticateUser(currentPassword)
            if (reauthSuccess) {
                val changeSuccess = repository.changeUserPassword(newPassword)
                if (changeSuccess) {
                    _updateResult.value = UpdateResult.Success
                } else {
                    _updateResult.value = UpdateResult.Error("No se pudo cambiar la contraseña.")
                }
            } else {
                _updateResult.value = UpdateResult.Error("La contraseña actual es incorrecta.")
            }
            _isLoading.value = false
        }
    }

    fun resetUpdateResult() {
        _updateResult.value = null
    }

    fun getCurrentUserEmail(): String {
        return auth.currentUser?.email ?: ""
    }

    fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }
}