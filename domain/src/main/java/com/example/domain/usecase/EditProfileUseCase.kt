package com.example.domain.usecase

import com.example.domain.repository.UserRepository

class EditProfileUseCase(private val userRepository: UserRepository) {
    suspend fun execute(email: String, name: String, avatarUrl: String) =
        userRepository.editProfile(email, name, avatarUrl)
}
