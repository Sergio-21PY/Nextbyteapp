package com.example.nextbyte_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextbyte_app.data.User
import com.example.nextbyte_app.data.UserRole
import com.example.nextbyte_app.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    // Cargar usuario actual
    fun loadCurrentUser(userId: String) {
        if (userId.isBlank()) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                _currentUser.value = repository.getUserById(userId)
                Log.d("UserViewModel", "Usuario cargado: ${_currentUser.value?.name}")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error cargando usuario: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
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

    // Actualizar perfil
    fun updateUserProfile(userId: String, name: String, phone: String, address: String) {
        viewModelScope.launch {
            try {
                val userData = mapOf(
                    "name" to name,
                    "phone" to phone,
                    "address" to address,
                    "updatedAt" to com.google.firebase.Timestamp.now()
                )
                val success = repository.updateUserProfile(userId, userData)
                if (success) {
                    // Recargar usuario actualizado
                    loadCurrentUser(userId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Actualizar rol (solo admin)
    fun updateUserRole(userId: String, newRole: UserRole) {
        viewModelScope.launch {
            try {
                val success = repository.updateUserRole(userId, newRole)
                if (success) {
                    // Recargar lista de usuarios
                    loadAllUsers()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Verificar permisos - VERSIÓN SIMPLIFICADA
    val canAddProducts: Boolean
        get() = currentUser.value?.let { user ->
            user.role == UserRole.ADMIN || user.role == UserRole.MANAGER
        } ?: false

    val canEditProducts: Boolean
        get() = currentUser.value?.let { user ->
            user.role == UserRole.ADMIN || user.role == UserRole.MANAGER
        } ?: false

    val canDeleteProducts: Boolean
        get() = currentUser.value?.let { user ->
            user.role == UserRole.ADMIN
        } ?: false

    val isAdmin: Boolean
        get() = currentUser.value?.role == UserRole.ADMIN

    val isManager: Boolean
        get() = currentUser.value?.role == UserRole.MANAGER

    // Cerrar sesión
    fun clearUser() {
        _currentUser.value = null
        _allUsers.value = emptyList()
    }
}