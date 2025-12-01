package com.example.nextbyte_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextbyte_app.data.User
import com.example.nextbyte_app.data.UserRole
import com.example.nextbyte_app.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

class UserViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // --- Address State ---
    private val _addresses = MutableStateFlow<List<String>>(emptyList())
    val addresses: StateFlow<List<String>> = _addresses.asStateFlow()

    private val _addressUpdateResult = MutableStateFlow<AuthViewModel.UpdateResult?>(null)
    val addressUpdateResult: StateFlow<AuthViewModel.UpdateResult?> = _addressUpdateResult

    // Cargar usuario actual
    fun loadCurrentUser(userId: String) {
        if (userId.isBlank()) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                _currentUser.value = repository.getUserById(userId)
                // También cargamos las direcciones del usuario
                if (_currentUser.value != null) {
                    loadAddresses(userId)
                }
                Log.d("UserViewModel", "Usuario cargado: ${_currentUser.value?.name}")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error cargando usuario: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- Address Functions ---
    fun loadAddresses(userId: String) {
        viewModelScope.launch {
            try {
                _addresses.value = repository.getUserAddresses(userId)
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error cargando direcciones: ${e.message}")
            }
        }
    }

    fun addAddress(userId: String, address: String) {
        viewModelScope.launch {
            if (address.isBlank()) {
                _addressUpdateResult.value = AuthViewModel.UpdateResult.Error("La dirección no puede estar vacía.")
                return@launch
            }
            val success = repository.addUserAddress(userId, address)
            if (success) {
                loadAddresses(userId) // Recargar lista
                _addressUpdateResult.value = AuthViewModel.UpdateResult.Success
            } else {
                _addressUpdateResult.value = AuthViewModel.UpdateResult.Error("No se pudo agregar la dirección.")
            }
        }
    }

    fun deleteAddress(userId: String, address: String) {
        viewModelScope.launch {
            val success = repository.deleteUserAddress(userId, address)
            if (success) {
                loadAddresses(userId) // Recargar lista
            } else {
                // Opcional: comunicar error a la UI
            }
        }
    }
    
    fun resetAddressUpdateResult() {
        _addressUpdateResult.value = null
    }

    // Cargar todos los usuarios (solo admin)
    fun loadAllUsers() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                _allUsers.value = repository.getAllUsers()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Actualizar rol (solo admin)
    fun updateUserRole(userId: String, newRole: UserRole) {
        viewModelScope.launch {
            val success = repository.updateUserRole(userId, newRole)
            if (success) {
                loadAllUsers()
            }
        }
    }

    fun clearUser() {
        _currentUser.value = null
        _allUsers.value = emptyList()
        _addresses.value = emptyList()
    }
}