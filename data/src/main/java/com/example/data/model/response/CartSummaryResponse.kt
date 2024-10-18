package com.example.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CartSummaryResponse(
    val `data`: Summary,
    val msg: String
) {
    fun toCartSummary() = com.example.domain.model.CartSummary(
        data = `data`.toSummaryData(),
        msg = msg
    )
}