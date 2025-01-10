package com.example.domain.usecase

import com.example.domain.repository.UserRepository
import com.example.domain.model.ProfileFormData
import com.example.domain.network.ResultWrapper

class EditProfileUseCase(private val userRepository: UserRepository) {
    suspend fun execute(formData: ProfileFormData): ResultWrapper<ProfileFormData> =
        userRepository.editProfile(formData)
}
