package com.example.data.model.response

import com.example.data.model.DataProductModel
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val data: DataProductModel,
    val msg: String
)
