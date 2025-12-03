package com.example.nextbyte_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextbyte_app.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StatsViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    // --- Datos de la UI ---
    private val _totalUsers = MutableStateFlow(0)
    val totalUsers: StateFlow<Int> = _totalUsers

    private val _totalProducts = MutableStateFlow(0)
    val totalProducts: StateFlow<Int> = _totalProducts

    private val _inventoryValue = MutableStateFlow(0.0)
    val inventoryValue: StateFlow<Double> = _inventoryValue

    private val _totalRevenue = MutableStateFlow(0.0)
    val totalRevenue: StateFlow<Double> = _totalRevenue

    private val _totalSales = MutableStateFlow(0)
    val totalSales: StateFlow<Int> = _totalSales

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Tareas en paralelo para máxima eficiencia
                val usersJob = launch { _totalUsers.value = repository.getAllUsers().size }
                val productsJob = launch {
                    val products = repository.getAllProducts()
                    _totalProducts.value = products.size
                    _inventoryValue.value = products.sumOf { it.price.toDouble() * it.stock }
                }
                val ordersJob = launch {
                    val orders = repository.getAllOrders()
                    _totalSales.value = orders.size
                    _totalRevenue.value = orders.sumOf { it.totalPrice }
                }
                
                // Esperar a que todas las tareas terminen
                usersJob.join()
                productsJob.join()
                ordersJob.join()

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
