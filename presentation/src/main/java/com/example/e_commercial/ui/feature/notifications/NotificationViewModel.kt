package com.example.e_commercial.ui.feature.notifications

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.OrdersData
import com.example.e_commercial.ui.feature.orders.OrdersEvent
import com.example.e_commercial.ui.feature.orders.OrdersViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val ordersViewModel: OrdersViewModel
) : ViewModel() {

    private val _orders = mutableStateListOf<OrdersData>()
    val orders: List<OrdersData> get() = _orders

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        observeOrders()
    }

    private fun observeOrders() {
        viewModelScope.launch {
            ordersViewModel.ordersEvent.collect { uiState ->
                isLoading = true
                try {
                    when (uiState) {
                        is OrdersEvent.Success -> {
                            val sortedOrders = uiState.data.sortedByDescending { it.orderDate }
                            _orders.clear()
                            _orders.addAll(sortedOrders)
                            errorMessage = null
                        }
                        is OrdersEvent.Error -> {
                            _orders.clear()
                            errorMessage = uiState.errorMsg
                        }
                        is OrdersEvent.Loading -> {
                            isLoading = true
                        }
                    }
                } catch (e: Exception) {
                    Log.e("NotificationViewModel", "Error observing orders: ${e.message}")
                    errorMessage = "An unexpected error occurred"
                } finally {
                    isLoading = false
                }
            }
        }
    }

    fun fetchOrders() {
        viewModelScope.launch {
            try {
                isLoading = true
                ordersViewModel.fetchOrders()
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error fetching orders: ${e.message}")
                errorMessage = "Failed to fetch orders. Please try again."
            } finally {
                isLoading = false
            }
        }
    }

    fun dismissOrder(orderId: Int) {
        _orders.removeIf { it.id == orderId }
    }
}
