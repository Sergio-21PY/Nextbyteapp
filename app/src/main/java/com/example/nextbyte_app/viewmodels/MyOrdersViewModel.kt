package com.example.nextbyte_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextbyte_app.data.Order
import com.example.nextbyte_app.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyOrdersViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadUserOrders(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _orders.value = repository.getOrdersForUser(userId)
            } finally {
                _isLoading.value = false
            }
        }
    }
}