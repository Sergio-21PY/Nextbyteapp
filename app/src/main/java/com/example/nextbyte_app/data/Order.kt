package com.example.nextbyte_app.data

import com.google.firebase.Timestamp

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val items: List<OrderItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val createdAt: Timestamp = Timestamp.now()
) {
    // Constructor sin argumentos para Firebase
    constructor() : this("", "", emptyList(), 0.0, Timestamp.now())
}

data class OrderItem(
    val productId: String = "",
    val name: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0
) {
    // Constructor sin argumentos para Firebase
    constructor() : this("", "", 0, 0.0)
}
