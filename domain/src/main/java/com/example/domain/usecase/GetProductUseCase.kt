package com.example.domain.usecase

import com.example.domain.repository.ProductRepository

class GetProductUseCase(private val repository: ProductRepository) {
    suspend fun execute(category: Int? = null) = repository.getProducts(category)
    suspend fun executeByCategory(categoryId: Int) = repository.getProductsByCategory(categoryId)
}
