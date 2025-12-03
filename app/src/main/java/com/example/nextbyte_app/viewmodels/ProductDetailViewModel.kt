package com.example.nextbyte_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextbyte_app.data.Product
import com.example.nextbyte_app.data.Review
import com.example.nextbyte_app.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadProductAndReviews(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val products = repository.getAllProducts()
                _product.value = products.find { it.id == productId }
                _reviews.value = repository.getReviewsForProduct(productId)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addReview(productId: String, review: Review) {
        viewModelScope.launch {
            val success = repository.addReview(productId, review)
            if (success) {
                // Después de añadir la reseña, actualizamos el promedio
                repository.updateProductAverageRating(productId)
                // Y recargamos todo para que la UI se actualice
                loadProductAndReviews(productId)
            }
        }
    }
}