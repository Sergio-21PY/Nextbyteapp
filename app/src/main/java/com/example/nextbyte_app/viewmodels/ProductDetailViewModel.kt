package com.example.nextbyte_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextbyte_app.data.Product
import com.example.nextbyte_app.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Asumimos que el repositorio puede obtener todos los productos
                val products = repository.getAllProducts()
                _product.value = products.find { it.id == productId }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}