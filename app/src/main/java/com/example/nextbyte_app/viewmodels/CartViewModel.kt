package com.example.nextbyte_app.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// --- Clases de datos para el estado del carrito ---

data class CartProduct(
    val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String
)

data class CartItem(
    val id: String,
    val product: CartProduct,
    val quantity: Int = 1
)

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val discountAmount: Double = 0.0,
    val discountPercentage: Int = 0,
    val total: Double = 0.0,
    val discountApplied: Boolean = false
)

// --- ViewModel ---

class CartViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val validDiscountCodes = mapOf(
        "PROMO10" to 10,
        "NEXTBYTE20" to 20,
        "DESCUENTO5" to 5,
        "PROMO20" to 20 // CÓDIGO AÑADIDO
    )

    fun addItem(product: CartProduct) {
        _uiState.update { currentState ->
            val existingItem = currentState.items.find { it.product.id == product.id }
            val updatedItems = if (existingItem != null) {
                currentState.items.map {
                    if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                currentState.items + CartItem(id = product.id, product = product, quantity = 1)
            }
            calculateTotals(currentState.copy(items = updatedItems))
        }
    }

    fun removeItem(itemId: String) {
        _uiState.update { currentState ->
            val updatedItems = currentState.items.filterNot { it.id == itemId }
            calculateTotals(currentState.copy(items = updatedItems))
        }
    }

    fun updateQuantity(itemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeItem(itemId)
            return
        }

        _uiState.update { currentState ->
            val updatedItems = currentState.items.map {
                if (it.id == itemId) it.copy(quantity = newQuantity) else it
            }
            calculateTotals(currentState.copy(items = updatedItems))
        }
    }

    fun applyDiscount(code: String): Boolean {
        val percentage = validDiscountCodes[code.uppercase()]
        val success = percentage != null

        _uiState.update { currentState ->
            if (success) {
                calculateTotals(currentState.copy(discountPercentage = percentage!!, discountApplied = true))
            } else {
                // Si el código no es válido, se quita el descuento
                calculateTotals(currentState.copy(discountPercentage = 0, discountApplied = false))
            }
        }
        return success
    }

    private fun calculateTotals(state: CartUiState): CartUiState {
        val subtotal = state.items.sumOf { it.product.price * it.quantity }
        val discountAmount = if (state.discountApplied) {
            (subtotal * state.discountPercentage) / 100
        } else {
            0.0
        }
        val total = subtotal - discountAmount
        return state.copy(
            subtotal = subtotal,
            discountAmount = discountAmount,
            total = total
        )
    }
}