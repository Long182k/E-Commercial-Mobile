package com.example.data.di

import com.example.data.repository.CategoryRepositoryImplement
import com.example.data.repository.ProductRepositoryImplement
import com.example.domain.repository.CartRepository
import com.example.domain.repository.CategoryRepository
import com.example.domain.repository.OrderRepository
import com.example.domain.repository.ProductRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<ProductRepository> { ProductRepositoryImplement(get()) }
    single<CategoryRepository> { CategoryRepositoryImplement(get()) }
    single<CartRepository> { com.example.data.repository.CartRepositoryImplement(get()) }
    single<OrderRepository> { com.example.data.repository.OrderRepositoryImplement(get()) }
}