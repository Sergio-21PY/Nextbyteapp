package com.example.nextbyte_app.ui.screens.settings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AddressViewModel : ViewModel() {
    val addresses = mutableStateOf(listOf("Dirección 1", "Dirección 2", "Dirección 3"))

    fun updateAddress(oldAddress: String, newAddress: String) {
        val index = addresses.value.indexOf(oldAddress)
        if (index != -1) {
            val newList = addresses.value.toMutableList()
            newList[index] = newAddress
            addresses.value = newList
        }
    }

    fun deleteAddress(address: String) {
        val newList = addresses.value.toMutableList()
        newList.remove(address)
        addresses.value = newList
    }
}