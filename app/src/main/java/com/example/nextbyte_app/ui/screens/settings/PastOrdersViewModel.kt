package com.example.nextbyte_app.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.nextbyte_app.data.Order
import com.example.nextbyte_app.data.pastOrders

class PastOrdersViewModel : ViewModel() {

    var searchQuery by mutableStateOf("")

    private var allOrders by mutableStateOf<List<Order>>(emptyList())

    var filteredOrders by mutableStateOf<List<Order>>(emptyList())
        private set

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
        // Si el usuario borra la búsqueda, mostramos todos los pedidos de nuevo.
        if (newQuery.isBlank()) {
            filteredOrders = allOrders
        }
    }

    fun performSearch() {
        filteredOrders = if (searchQuery.isBlank()) {
            allOrders
        } else {
            allOrders.filter {
                // La búsqueda por nombre de usuario sigue siendo flexible
                it.userName.contains(searchQuery, ignoreCase = true) ||
                // La búsqueda por ID ahora debe ser exacta
                it.orderId.equals(searchQuery, ignoreCase = true)
            }
        }
    }

    fun loadOrders(userEmail: String, isAdmin: Boolean) {
        allOrders = if (isAdmin) {
            pastOrders // El admin ve todos los pedidos
        } else {
            pastOrders.filter { it.userEmail == userEmail } // El usuario solo ve los suyos
        }
        filteredOrders = allOrders // Al inicio, la lista filtrada es igual a la completa.
    }
}
