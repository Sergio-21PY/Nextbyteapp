package com.example.nextbyte_app.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextbyte_app.data.Product
import com.example.nextbyte_app.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    // Lista privada que siempre contiene TODOS los productos.
    private var allProducts: List<Product> = emptyList()

    // StateFlow que la UI observa. Contendrá la lista filtrada o la completa.
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState

    sealed class UploadState {
        object Idle : UploadState()
        object Loading : UploadState()
        data class Success(val productId: String) : UploadState()
        data class Error(val message: String) : UploadState()
    }

    init {
        loadProducts()
        loadCategories()
    }

    fun loadProducts() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                allProducts = repository.getAllProducts()
                _products.value = allProducts // Inicialmente, mostrar todos
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // <<-- NUEVA FUNCIÓN DE FILTRADO -->>
    fun filterByCategory(category: String) {
        if (category.equals("Todos", ignoreCase = true)) {
            _products.value = allProducts
        } else {
            _products.value = allProducts.filter { it.category.equals(category, ignoreCase = true) }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            val success = repository.updateProduct(product)
            if (success) {
                loadProducts()
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            val success = repository.deleteProduct(productId)
            if (success) {
                loadProducts()
            }
        }
    }

    fun uploadProductImage(imageUri: Uri, product: Product, isEditMode: Boolean) {
        _uploadState.value = UploadState.Loading
        viewModelScope.launch {
            val imageUrl = repository.uploadImage(imageUri)
            if (imageUrl.isNotEmpty()) {
                val newProduct = product.copy(imageUrl = imageUrl)
                if (isEditMode) {
                    updateProduct(newProduct)
                } else {
                    addProduct(newProduct)
                }
            } else {
                _uploadState.value = UploadState.Error("No se pudo subir la imagen.")
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            try {
                _categories.value = repository.getCategories()
            } catch (e: Exception) {
                e.printStackTrace()
                _categories.value = getDefaultCategories()
            }
        }
    }

    fun addProduct(product: Product) {
        _uploadState.value = UploadState.Loading

        viewModelScope.launch {
            try {
                val productId = repository.addProduct(product)
                _uploadState.value = UploadState.Success(productId)
                loadProducts()

            } catch (e: Exception) {
                _uploadState.value = UploadState.Error("Error al agregar producto: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun resetUploadState() {
        _uploadState.value = UploadState.Idle
    }

    fun refreshProducts() {
        loadProducts()
    }

    private fun getDefaultCategories(): List<String> {
        return listOf(
            "Smartphones",
            "Laptops",
            "Tablets",
            "Accessories",
            "Gaming",
            "Audio",
            "Cameras",
            "Wearables"
        )
    }
}
