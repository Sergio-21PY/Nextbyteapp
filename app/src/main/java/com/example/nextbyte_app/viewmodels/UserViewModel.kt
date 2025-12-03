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

    // --- General Profile Update State ---
    private val _updateResult = MutableStateFlow<AuthViewModel.UpdateResult?>(null)
    val updateResult: StateFlow<AuthViewModel.UpdateResult?> = _updateResult

    // --- Address State ---
    private val _addresses = MutableStateFlow<List<String>>(emptyList())
    val addresses: StateFlow<List<String>> = _addresses.asStateFlow()

    private val _addressUpdateResult = MutableStateFlow<AuthViewModel.UpdateResult?>(null)
    val addressUpdateResult: StateFlow<AuthViewModel.UpdateResult?> = _addressUpdateResult

    fun loadCurrentUser(userId: String) {
        if (userId.isBlank()) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val user = repository.getUserById(userId)
                _currentUser.value = user
                if (user != null) {
                    loadAddresses(user.uid)
                }
                Log.d("UserViewModel", "Usuario cargado: ${user?.name}")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error cargando usuario: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- Profile Update Functions ---
    fun updatePhoneNumber(userId: String, newPhoneNumber: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.updateUserProfile(userId, mapOf("phone" to newPhoneNumber))
            if (success) {
                loadCurrentUser(userId)
                _updateResult.value = AuthViewModel.UpdateResult.Success
            } else {
                _updateResult.value = AuthViewModel.UpdateResult.Error("No se pudo actualizar el número de teléfono.")
            }
            _isLoading.value = false
        }
    }

    fun resetUpdateResult() {
        _updateResult.value = null
    }
    
    // <<-- NUEVA FUNCIÓN PARA FAVORITOS -->>
    fun toggleFavorite(productId: String) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val isCurrentlyFavorite = user.favoriteProductIds.contains(productId)

            val success = if (isCurrentlyFavorite) {
                repository.removeFavorite(user.uid, productId)
            } else {
                repository.addFavorite(user.uid, productId)
            }

            if (success) {
                // Actualizamos el estado localmente para una respuesta instantánea en la UI
                val updatedFavorites = if (isCurrentlyFavorite) {
                    user.favoriteProductIds - productId
                } else {
                    user.favoriteProductIds + productId
                }
                _currentUser.value = user.copy(favoriteProductIds = updatedFavorites)
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
                loadAddresses(userId) 
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
                loadAddresses(userId)
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