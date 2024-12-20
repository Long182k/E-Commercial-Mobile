package com.example.e_commercial.ui.feature.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.OrdersData
import com.example.domain.model.UserDomainModel
import com.example.domain.usecase.OrderListUseCase
import com.example.e_commercial.EcommercialSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val orderListUseCase: OrderListUseCase
) : ViewModel() {

    private val _ordersEvent = MutableStateFlow<OrdersEvent>(OrdersEvent.Loading)
    val ordersEvent = _ordersEvent.asStateFlow()

    private val _user = MutableStateFlow<UserDomainModel?>(null)
    val user: StateFlow<UserDomainModel?> = _user.asStateFlow()

    init {
        _user.value = EcommercialSession.getUser()
        getOrderList() // Initial fetch
    }

    fun fetchOrders() {
        getOrderList() // Expose the internal method for refetching
    }

    fun filterOrders(list: List<OrdersData>, filter: String): List<OrdersData> {
        return list.filter { it.status == filter }
    }

    private fun getOrderList() {
        viewModelScope.launch {
            val userId = _user.value?.id?.toLong() ?: return@launch
            val result = orderListUseCase.execute(userId)

            when (result) {
                is com.example.domain.network.ResultWrapper.Success -> {
                    val data = result.value
                    val ordersWithAddress = data.data.map { order ->
                        OrdersData(
                            id = order.id,
                            items = order.items,
                            orderDate = order.orderDate,
                            status = order.status,
                            totalAmount = order.totalAmount,
                            userId = order.userId,
                            address = order.address,
                        )
                    }
                    _ordersEvent.value = OrdersEvent.Success(ordersWithAddress)
                }

                is com.example.domain.network.ResultWrapper.Failure -> {
                    _ordersEvent.value = OrdersEvent.Error("Something went wrong")
                }
            }
        }
    }
}

sealed class OrdersEvent {
    object Loading : OrdersEvent()
    data class Success(val data: List<OrdersData>) : OrdersEvent()
    data class Error(val errorMsg: String) : OrdersEvent()
}
