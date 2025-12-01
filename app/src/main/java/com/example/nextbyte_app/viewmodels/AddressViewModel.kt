package com.example.nextbyte_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextbyte_app.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddressViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    private val _addresses = MutableStateFlow<List<String>>(emptyList())
    val addresses: StateFlow<List<String>> = _addresses.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Se necesita el ID del usuario para obtener sus direcciones
    fun loadAddresses(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val userAddresses = repository.getUserAddresses(userId)
            _addresses.value = userAddresses
            _isLoading.value = false
        }
    }

    fun addAddress(userId: String, address: String) {
        viewModelScope.launch {
            if (repository.addUserAddress(userId, address)) {
                _addresses.update { it + address }
            }
        }
    }

    fun deleteAddress(userId: String, address: String) {
        viewModelScope.launch {
            if (repository.deleteUserAddress(userId, address)) {
                _addresses.update { it - address }
            }
        }
    }
}