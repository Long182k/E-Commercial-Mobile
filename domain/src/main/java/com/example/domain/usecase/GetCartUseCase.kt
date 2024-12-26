package com.example.domain.usecase

import com.example.domain.repository.CartRepository

open class GetCartUseCase(val cartRepository: CartRepository) {
    suspend fun execute(userId: Long) = cartRepository.getCart(userId)
}
