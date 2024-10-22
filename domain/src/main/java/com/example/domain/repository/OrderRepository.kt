package com.example.domain.repository

import com.example.domain.model.AddressDomainModel
import com.example.domain.model.OrdersListModel
import com.example.domain.network.ResultWrapper

interface OrderRepository {
    suspend fun placeOrder(addressDomainModel: AddressDomainModel): ResultWrapper<Long>
    suspend fun getOrderList(): ResultWrapper<OrdersListModel>
}