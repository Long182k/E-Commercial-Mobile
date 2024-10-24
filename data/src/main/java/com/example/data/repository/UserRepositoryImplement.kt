package com.example.data.repository

import com.example.domain.network.NetworkService
import com.example.domain.repository.UserRepository

class UserRepositoryImplement(private val networkService: NetworkService) : UserRepository {
    override suspend fun register(email: String, password: String, name: String) =
        networkService.register(email, password, name)

    override suspend fun login(email: String, password: String) =
        networkService.login(email, password)
}