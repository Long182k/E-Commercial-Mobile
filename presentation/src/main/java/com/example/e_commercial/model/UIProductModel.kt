package com.example.e_commercial.model

import android.os.Parcelable
import com.example.domain.model.Product
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class UIProductModel(
    val id: Int,
    val title: String,
    val price: Double,
    val categoryId: Int,
    val description: String,
    val image: String
) : Parcelable {
    companion object {
        fun fromProduct(product: Product) = UIProductModel(
            id = product.id,
            title = product.title,
            price = product.price,
            categoryId = product.categoryId,
            description = product.description,
            image = product.image
        )
    }

}

