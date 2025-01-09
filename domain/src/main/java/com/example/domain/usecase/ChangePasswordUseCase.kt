package com.example.domain.usecase

import com.example.domain.network.NetworkService
import com.example.domain.network.ResultWrapper

class ChangePasswordUseCase(private val networkService: NetworkService) {
    suspend operator fun invoke(email: String, oldPassword: String, newPassword: String): ResultWrapper<Unit> {
        return networkService.changePassword(email, oldPassword, newPassword)
    }
}
