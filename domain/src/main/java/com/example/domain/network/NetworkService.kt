package com.example.domain.network

import com.example.domain.model.CartItemModel
import com.example.domain.model.CartModel
import com.example.domain.model.CategoriesListModel
import com.example.domain.model.Product
import com.example.domain.model.ProductListModel
import com.example.domain.model.request.AddCartRequestModel

interface NetworkService {
    suspend fun getProducts(category: Int?): ResultWrapper<ProductListModel>
    suspend fun getCategories(): ResultWrapper<CategoriesListModel>

    suspend fun addProductToCart(
        request: AddCartRequestModel
    ) : ResultWrapper<CartModel>

    suspend fun getCart() : ResultWrapper<CartModel>
}

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class Failure(val exception: Exception) : ResultWrapper<Nothing>()
}