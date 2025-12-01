package com.example.nextbyte_app.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddressViewModel : ViewModel() {

    private val _addresses = MutableStateFlow<List<String>>(listOf(
        "Av. Libertador 123, Piso 4, Apto 401, CABA",
        "Calle Falsa 123, Springfield",
        "Casa de mi madre, Chile"
    ))
    val addresses: StateFlow<List<String>> = _addresses.asStateFlow()

    fun addAddress(address: String) {
        _addresses.update { it + address }
    }

    fun deleteAddress(address: String) {
        _addresses.update { currentAddresses -> currentAddresses.filter { it != address } }
    }
}