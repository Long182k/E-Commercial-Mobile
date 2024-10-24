package com.example.domain.usecase

import com.example.domain.repository.UserRepository

class RegisterUseCase(private val userRepository: UserRepository) {
    suspend fun execute(username: String, password: String, name: String) =
        userRepository.register(username, password, name)
}