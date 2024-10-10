package com.example.data.repository
import com.example.domain.model.Product
import com.example.domain.model.ProductListModel
import com.example.domain.repository.ProductRepository
import com.example.domain.network.NetworkService
import com.example.domain.network.ResultWrapper

class ProductRepositoryImplement(private val networkService: NetworkService): ProductRepository {
    override suspend fun getProducts(category: Int?): ResultWrapper<ProductListModel> {
        return networkService.getProducts(category)
    }
}