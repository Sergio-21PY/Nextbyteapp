package com.example.nextbyte_app.data

data class Product(
    val id: String = "",  // IMPORTANTE: agregar ID para Firebase
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val category: String = "",
    val stock: Int = 0,
    val rating: Double = 0.0,  // Agregar rating
    val isFavorited: Boolean = false
) {
    // Constructor sin parámetros para Firebase
    constructor() : this("", "", "", 0.0, "", "", 0, 0.0)
}