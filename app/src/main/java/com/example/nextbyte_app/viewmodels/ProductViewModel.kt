package com.example.nextbyte_app.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextbyte_app.data.Product
import com.example.nextbyte_app.data.Review
import com.example.nextbyte_app.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel ÚNICO y centralizado para toda la lógica de productos.
 */
class ProductViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    // --- ESTADO GENERAL ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // --- LISTA DE PRODUCTOS (para Home y pantalla de Productos) ---
    private var allProducts: List<Product> = emptyList()
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    // --- DETALLE DE PRODUCTO (para ProductDetailScreen) ---
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    // --- ESTADO DE SUBIDA (para Add/Edit Product) ---
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
    }

    fun loadProducts() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                allProducts = repository.getAllProducts()
                _products.value = allProducts
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterByCategory(category: String) {
        _products.value = if (category.equals("Todos", ignoreCase = true)) {
            allProducts
        } else {
            allProducts.filter { it.category.equals(category, ignoreCase = true) }
        }
    }

    // <<-- NUEVA LÓGICA CENTRALIZADA -->>

    fun loadProductDetails(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Primero busca en la lista que ya tenemos
                var product = allProducts.find { it.id == productId }
                // Si no está (o la lista está vacía), la cargamos de nuevo
                if (product == null) {
                    loadProducts()
                    product = allProducts.find { it.id == productId }
                }
                _selectedProduct.value = product
                _reviews.value = repository.getReviewsForProduct(productId)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addReview(productId: String, review: Review) {
        viewModelScope.launch {
            val success = repository.addReview(productId, review)
            if (success) {
                repository.updateProductAverageRating(productId)
                // Forzamos la recarga de TODO para que la HomeScreen se actualice
                loadProducts()
                // Y también recargamos los detalles de la pantalla actual
                loadProductDetails(productId)
            }
        }
    }
    
    // Resto de funciones existentes...
    fun updateProduct(product: Product) {
        viewModelScope.launch {
            val success = repository.updateProduct(product)
            if (success) loadProducts()
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            val success = repository.deleteProduct(productId)
            if (success) loadProducts()
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

    fun addProduct(product: Product) {
        _uploadState.value = UploadState.Loading
        viewModelScope.launch {
            try {
                val productId = repository.addProduct(product)
                _uploadState.value = UploadState.Success(productId)
                loadProducts()
            } catch (e: Exception) {
                _uploadState.value = UploadState.Error("Error al agregar producto: ${e.message}")
            }
        }
    }

    fun resetUploadState() {
        _uploadState.value = UploadState.Idle
    }
}