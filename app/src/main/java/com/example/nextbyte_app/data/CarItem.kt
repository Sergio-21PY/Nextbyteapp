package com.example.nextbyte_app.data

data class CartItem(
    val product: Product,
    var quantity: Int = 1
)