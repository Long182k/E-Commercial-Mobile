package com.example.data.model.response

import com.example.domain.model.OrderProductItem
import kotlinx.serialization.Serializable

@Serializable
data class OrderItem(
    val id: Int,
    val orderId: Int,
    val price: Double,
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val userId: Int
) {
    suspend fun toDomainResponse(
        getProductImage: suspend (Int) -> String
    ): OrderProductItem {
        val image = try {
            getProductImage(productId)
        } catch (e: Exception) {
            "https://via.placeholder.com/150"
        }

        return OrderProductItem(
            id = id,
            orderId = orderId,
            price = price,
            productId = productId,
            productName = productName,
            quantity = quantity,
            userId = userId,
            image = image
        )
    }
}
