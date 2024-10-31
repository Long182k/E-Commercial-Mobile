package com.example.data.model.response

import com.example.domain.model.CartItemModel
import com.example.domain.model.SummaryData
import kotlinx.serialization.Serializable

@Serializable
data class SummaryDataResponse(
    val discount: Double,
    val items: List<CartItem>,
    val shipping: Double,
    val subtotal: Double,
    val tax: Double,
    val total: Double
) {
    fun toDomain(): SummaryData {
        return SummaryData(
            discount = discount,
            items = items.map { it.toCartItemModel() }, // Assuming CartItemResponse has a toDomain() method
            shipping = shipping,
            subtotal = subtotal,
            tax = tax,
            total = total
        )
    }
}
