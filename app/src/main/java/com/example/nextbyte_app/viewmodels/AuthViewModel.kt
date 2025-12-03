package com.example.nextbyte_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextbyte_app.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel robusto para gestionar la autenticación.
 * Combina un listener en tiempo real para el estado de la sesión con la lógica 
 * para ejecutar acciones como cambiar la contraseña.
 */
class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val repository = FirebaseRepository()

    // --- LA ÚNICA FUENTE DE VERDAD PARA EL ESTADO DE SESIÓN ---
    val authState: StateFlow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser).isSuccess
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), auth.currentUser)

    // --- ESTADOS PARA LA UI (feedback de operaciones) ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _updateResult = MutableStateFlow<UpdateResult?>(null)
    val updateResult: StateFlow<UpdateResult?> = _updateResult

    sealed class UpdateResult {
        object Success : UpdateResult()
        data class Error(val message: String) : UpdateResult()
    }

    // --- ACCIONES DEL USUARIO ---

    fun logout() {
        auth.signOut()
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

    fun getCurrentUserId(): String {
        return authState.value?.uid ?: ""
    }
}
