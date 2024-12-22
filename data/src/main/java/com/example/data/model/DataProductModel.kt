package com.example.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DataProductModel(
    val categoryId: Int? = null,
    val description: String? = null,
    val id: Int? = null,
    val image: String? = null,
    val price: Double? = null,
    val title: String? = null,
) {
    fun toProduct() = com.example.domain.model.Product(
        id = id ?: -1,
        title = title ?: "Unknown Title",
        price = price ?: 0.0,
        categoryId = categoryId ?: 0,
        description = description ?: "No description",
        image = image ?: "https://via.placeholder.com/150"
    )
}
