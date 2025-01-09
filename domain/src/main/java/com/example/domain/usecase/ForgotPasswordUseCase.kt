package com.example.domain.usecase

import com.example.domain.repository.UserRepository

class ForgotPasswordUseCase(private val userRepository: UserRepository) {
    suspend fun execute(email: String) =
        userRepository.forgotPassword(email)
}
