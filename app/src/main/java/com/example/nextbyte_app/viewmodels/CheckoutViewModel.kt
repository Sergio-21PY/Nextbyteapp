package com.example.nextbyte_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextbyte_app.data.Order
import com.example.nextbyte_app.data.OrderItem
import com.example.nextbyte_app.data.OrderStatus
import com.example.nextbyte_app.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CheckoutViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    private val _checkoutState = MutableStateFlow<CheckoutState>(CheckoutState.Idle)
    val checkoutState: StateFlow<CheckoutState> = _checkoutState

    sealed class CheckoutState {
        object Idle : CheckoutState()
        object Processing : CheckoutState()
        object Success : CheckoutState()
        data class Error(val message: String) : CheckoutState()
    }

    fun placeOrder(cartUiState: CartUiState, userId: String) {
        viewModelScope.launch {
            _checkoutState.value = CheckoutState.Processing

            val order = Order(
                orderId = UUID.randomUUID().toString(),
                userId = userId,
                items = cartUiState.items.map {
                    OrderItem(it.product.id, it.product.name, it.quantity, it.product.price)
                },
                totalPrice = cartUiState.total,
                status = OrderStatus.PROCESANDO // <<-- ESTADO INICIAL ASEGURADO
            )

            val success = repository.saveOrder(order)

            _checkoutState.value = if (success) {
                CheckoutState.Success
            } else {
                CheckoutState.Error("No se pudo procesar la orden.")
            }
        }
    }

    fun resetCheckoutState() {
        _checkoutState.value = CheckoutState.Idle
    }
}