package com.example.domain.model

data class OrdersData(
    val id: Int,
    val items: List<OrderProductItem>,
    val orderDate: String,
    val status: String,
    val totalAmount: Double,
    val userId: Int,
    val address: AddressDomainModel,
    val subtotal: Double,
    val shipping: Double,
    val tax: Double,
    val discount: Double
)