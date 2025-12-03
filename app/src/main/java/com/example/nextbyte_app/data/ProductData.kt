package com.example.nextbyte_app.data

// El modelo de datos definitivo y robusto. Los precios son Double para evitar crashes.
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val code: String = "",      // Código/SKU del producto
    val price: Double = 0.0,    // CORREGIDO: Precio de venta es Double para compatibilidad
    val cost: Double = 0.0,       // CORREGIDO: Precio de costo es Double para compatibilidad
    val imageUrl: String = "",
    val category: String = "",
    val stock: Int = 0,
    val rating: Double = 0.0,
    val isFavorited: Boolean = false
) {
    // Constructor sin parámetros para Firebase
    constructor() : this("", "", "", "", 0.0, 0.0, "", "", 0, 0.0)
}
