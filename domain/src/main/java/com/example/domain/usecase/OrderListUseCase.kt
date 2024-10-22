package com.example.domain.usecase

import com.example.domain.model.OrdersListModel
import com.example.domain.repository.OrderRepository

class OrderListUseCase(
    private val repository: OrderRepository
) {
    suspend fun execute() = repository.getOrderList()
}