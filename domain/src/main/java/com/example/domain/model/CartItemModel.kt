package com.example.domain.model

data class CartItemModel(
    val id: Int,
    val productId: Int,
    val userId: Int,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String?,
    val productName: String
)