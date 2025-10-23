package com.example.nextbyte_app.ui.screens.carrito

import com.example.nextbyte_app.data.Product
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.nextbyte_app.data.CartItem

/*Logica de carrito de compras*/
class CartViewModel: ViewModel() {
    /* Lista privada donde guardamos los productos,
    * el guion bajo es solo para decir que es una variable interna y privada.*/

    private val _cartItems = mutableStateListOf<CartItem>()

    /* La lista de productos publica, donde solo podran visualizar los productos en
    * en la pantalla de CarritoScreen.*/
    val cartItems: List<CartItem> = _cartItems

    /*Variable que utilizaremos para realizar el calculo de los valores de los productos,
    * utilizamos una derivedStateOf cuando se recalcula de forma automatica cuando un item cambia*/
    val total by derivedStateOf {
        _cartItems.sumOf { it.product.price * it.quantity }
    }

    // Funcion agregar producto al carrito.
    fun addProduct(product: Product) {
        // Busca el ÍNDICE del producto en la lista
        val existingItemIndex = _cartItems.indexOfFirst { it.product.id == product.id }

        if (existingItemIndex != -1) {
            // --- INICIO DEL ARREGLO ---
            // Si existe, obtén el item antiguo
            val existingItem = _cartItems[existingItemIndex]

            // Crea un objeto *NUEVO* (una copia) con la cantidad aumentada
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)

            // *Reemplaza* el item antiguo por el nuevo en la lista
            // Esto SÍ dispara la actualización de la UI
            _cartItems[existingItemIndex] = updatedItem


        } else {
            // Si no existe, lo añade como un nuevo item
            _cartItems.add(CartItem(product = product, quantity = 1))
        }
    }

    // Funcion para remover productos del carrito.
    fun removeProduct(product: Product) {
        _cartItems.removeAll { it.product.id == product.id }
    }

    fun updateQuantity(product: Product, newQuantity: Int) {
        // Si la cantidad es 0 o menos, elimínalo
        if (newQuantity <= 0) {
            removeProduct(product)
            return
        }

        //
        val existingItemIndex = _cartItems.indexOfFirst { it.product.id == product.id }

        if (existingItemIndex != -1) {

            // obtiene el item antiguo
            val existingItem = _cartItems[existingItemIndex]

            // Crea un objeto NUEVO (una copia) con la nueva cantidad
            val updatedItem = existingItem.copy(quantity = newQuantity)

            // *Reemplaza* el item antiguo por el nuevo
            _cartItems[existingItemIndex] = updatedItem

        }
    }

    //Funcion para logica de pago.
    fun processCheckout() {
        // Aquí iría tu lógica para procesar el pago
        println("Procesando pago por un total de: $total")
        // Para limpiar el carrito de compras podemos utilizar,
        // _cartItems.clear()

        //PD AGREGAR BOTON DE COMPRAR.
    }
}