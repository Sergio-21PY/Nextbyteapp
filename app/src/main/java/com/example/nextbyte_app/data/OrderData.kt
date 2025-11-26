package com.example.nextbyte_app.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// Modelo de datos para un pedido
data class Order(
    val orderId: String = UUID.randomUUID().toString().substring(0, 8),
    val userEmail: String,
    val userName: String,
    val items: List<CartItem>,
    val totalAmount: Int,
    val orderDate: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
)

// Datos de prueba para simular pedidos anteriores
val pastOrders = listOf(
    Order(
        userEmail = "user@user.cl",
        userName = "Usuario de Prueba",
        items = listOf(CartItem(allProducts[0], 1)),
        totalAmount = 39999
    ),
    Order(
        userEmail = "otro@user.cl",
        userName = "Otro Cliente",
        items = listOf(CartItem(allProducts[1], 1), CartItem(allProducts[2], 1)),
        totalAmount = 300998
    ),
    Order(
        userEmail = "user@user.cl",
        userName = "Usuario de Prueba",
        items = listOf(CartItem(allProducts[2], 1)),
        totalAmount = 220999
    ),
    Order(
        userEmail = "admin@admin.cl",
        userName = "Administrador",
        items = listOf(CartItem(allProducts[0], 2)),
        totalAmount = 79998
    )
)
