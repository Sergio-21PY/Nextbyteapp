package com.example.nextbyte_app.ui.screens.carrito

import androidx.lifecycle.ViewModel
import com.example.nextbyte_app.data.Product

class CartViewModel : ViewModel() {
    private val _cartItems = mutableListOf<Product>()
    val cartItems: List<Product> get() = _cartItems

    fun addProduct(product: Product) {
        _cartItems.add(product)
    }

    fun removeProduct(product: Product) {
        _cartItems.remove(product)
    }

    fun clearCart() {
        _cartItems.clear()
    }

    fun getTotalPrice(): Double {
        return _cartItems.sumOf { it.price }
    }
}
