package com.example.data.model.response

import com.example.domain.model.OrdersData
import kotlinx.serialization.Serializable

@Serializable
data class OrderListData(
    val id: Int,
    val items: List<OrderItem>,
    val orderDate: String,
    val status: String,
    val totalAmount: Double,
    val userId: Int,
    val address: AddressResponse,
    val subtotal: Double,
    val shipping: Double,
    val tax: Double,
    val discount: Double
) {
    suspend fun toDomainResponse(
        getProductImage: suspend (Int) -> String
    ): OrdersData {
        return OrdersData(
            id = id,
            items = items.map { it.toDomainResponse(getProductImage) }, // Each item fetches its image
            orderDate = orderDate,
            status = status,
            totalAmount = totalAmount,
            userId = userId,
            address = address.toDomain(),
            subtotal = subtotal,
            shipping = shipping,
            tax = tax,
            discount = discount
        )
    }
}
