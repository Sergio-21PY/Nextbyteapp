package com.example.nextbyte_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextbyte_app.data.Product
import com.example.nextbyte_app.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditProductViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    fun getProductById(productId: String) {
        viewModelScope.launch {
            // Suponiendo que tienes una función en tu repositorio para obtener un producto por ID
            val product = repository.getAllProducts().find { it.id == productId } 
            _product.value = product
        }
    }
}