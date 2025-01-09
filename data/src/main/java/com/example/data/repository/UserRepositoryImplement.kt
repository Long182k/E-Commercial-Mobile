package com.example.data.repository

import com.example.domain.network.NetworkService
import com.example.domain.network.ResultWrapper
import com.example.domain.repository.UserRepository

class UserRepositoryImplement(private val networkService: NetworkService) : UserRepository {
    override suspend fun register(email: String, password: String, name: String) =
        networkService.register(email, password, name)

    override suspend fun login(email: String, password: String) =
        networkService.login(email, password)

    override suspend fun changePassword(email: String, oldPassword: String, newPassword: String) {
        networkService.changePassword(email, oldPassword, newPassword)
    }

    override suspend fun forgotPassword(email: String): ResultWrapper<Unit> {
        return networkService.forgotPassword(email)
    }
}
