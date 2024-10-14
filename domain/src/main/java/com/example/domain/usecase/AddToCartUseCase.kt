package com.example.domain.usecase

import com.example.domain.model.request.AddCartRequestModel
import com.example.domain.repository.CartRepository

class AddToCartUseCase(private val cartRepository: CartRepository) {
    suspend fun execute(request: AddCartRequestModel) = cartRepository.addProductToCart(request)
}