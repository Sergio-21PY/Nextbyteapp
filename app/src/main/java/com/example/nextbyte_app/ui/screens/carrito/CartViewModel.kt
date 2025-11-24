package com.example.nextbyte_app.ui.screens.carrito

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.nextbyte_app.data.CartItem
import com.example.nextbyte_app.data.Product
import androidx.compose.runtime.mutableStateListOf

class CartViewModel : ViewModel() {
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> = _cartItems

    // Estados para el código de descuento
    var discountCode by mutableStateOf("")
    var discountAmount by mutableStateOf(0)
        private set // Solo el ViewModel puede modificarlo

    val total by derivedStateOf {
        _cartItems.sumOf { it.product.price * it.quantity }
    }

    val finalTotal by derivedStateOf {
        (total - discountAmount).coerceAtLeast(0)
    }

    fun applyDiscountCode(): String {
        return if (discountCode.equals("PROMO20", ignoreCase = true)) {
            discountAmount = (total * 0.2).toInt()
            discountCode = ""
            "Descuento aplicado con éxito"
        } else {
            discountAmount = 0
            "El código de descuento no es válido"
        }
    }

    fun addProduct(product: Product) {
        val existingItemIndex = _cartItems.indexOfFirst { it.product.id == product.id }
        if (existingItemIndex != -1) {
            val existingItem = _cartItems[existingItemIndex]
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
            _cartItems[existingItemIndex] = updatedItem
        } else {
            _cartItems.add(CartItem(product = product, quantity = 1))
        }
    }

    fun removeProduct(product: Product) {
        _cartItems.removeAll { it.product.id == product.id }
    }

    fun updateQuantity(product: Product, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeProduct(product)
            return
        }
        val existingItemIndex = _cartItems.indexOfFirst { it.product.id == product.id }
        if (existingItemIndex != -1) {
            val existingItem = _cartItems[existingItemIndex]
            val updatedItem = existingItem.copy(quantity = newQuantity)
            _cartItems[existingItemIndex] = updatedItem
        }
    }

    fun processCheckout() {
        println("Procesando pago por un total de: $finalTotal")
        // Lógica para limpiar el carrito, etc.
    }
}
