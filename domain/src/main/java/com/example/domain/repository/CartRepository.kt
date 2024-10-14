package com.example.domain.repository

import com.example.domain.model.CartItemModel
import com.example.domain.model.CartModel
import com.example.domain.model.request.AddCartRequestModel
import com.example.domain.network.ResultWrapper

interface CartRepository {
    suspend fun addProductToCart(
        request: AddCartRequestModel
    ) : ResultWrapper<CartModel>
    suspend fun getCart(): ResultWrapper<CartModel>
}

