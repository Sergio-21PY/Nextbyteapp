package com.example.nextbyte_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextbyte_app.data.Order
import com.example.nextbyte_app.data.OrderStatus
import com.example.nextbyte_app.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ManageOrdersViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    private val _allOrders = MutableStateFlow<List<Order>>(emptyList())
    val allOrders: StateFlow<List<Order>> = _allOrders

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadAllOrders()
    }

    fun loadAllOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Obtenemos TODAS las órdenes para el panel de admin
                _allOrders.value = repository.getAllOrders()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        viewModelScope.launch {
            val success = repository.updateOrderStatus(orderId, newStatus)
            if (success) {
                // Si el cambio en la BD fue exitoso, recargamos la lista
                loadAllOrders()
            }
        }
    }
}