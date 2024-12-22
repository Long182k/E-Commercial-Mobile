package com.example.data.model.response

import com.example.domain.model.OrdersListModel
import kotlinx.serialization.Serializable

@Serializable
data class OrdersListResponse(
    val `data`: List<OrderListData>,
    val msg: String
) {
    suspend fun toDomainResponse(
        getProductImage: suspend (Int) -> String
    ): OrdersListModel {
        return OrdersListModel(
            data = data.map { order ->
                order.toDomainResponse(getProductImage)
            },
            msg = msg
        )
    }
}
