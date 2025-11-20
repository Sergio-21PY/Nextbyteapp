package com.example.nextbyte_app.ui.screens.settings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AddressViewModel : ViewModel() {
    // Solo una dirección, que puede ser nula si no existe
    val address = mutableStateOf<String?>(null) // Comienza vacía

    fun updateAddress(newAddress: String) {
        address.value = newAddress
    }

    fun addAddress(newAddress: String) {
        address.value = newAddress
    }

    fun deleteAddress() {
        address.value = null
    }
}