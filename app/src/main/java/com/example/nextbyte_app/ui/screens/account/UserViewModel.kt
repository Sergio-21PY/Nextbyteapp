package com.example.nextbyte_app.ui.screens.account

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UserState(
    val name: String,
    val email: String,
    val profileImageUri: Uri?,
    val isAdmin: Boolean
)

class UserViewModel : ViewModel() {
    private val _user = MutableStateFlow<UserState?>(null)
    val user: StateFlow<UserState?> = _user.asStateFlow()

    fun updateUser(newName: String, newProfileImageUri: Uri?) {
        _user.update { currentState ->
            currentState?.copy(
                name = newName,
                profileImageUri = newProfileImageUri ?: currentState.profileImageUri
            )
        }
    }

    fun login(email: String, pass: String): Boolean {
        if (email == "admin@admin.cl" && pass == "1234") {
            _user.value = UserState(
                name = "Administrador",
                email = "admin@admin.cl",
                profileImageUri = null,
                isAdmin = true
            )
            return true
        } else if (email == "user@user.cl" && pass == "1234") {
             _user.value = UserState(
                name = "Usuario",
                email = "user@user.cl",
                profileImageUri = null,
                isAdmin = false
            )
            return true
        }
        _user.value = null
        return false
    }

    fun logout() {
        _user.value = null
    }
}
