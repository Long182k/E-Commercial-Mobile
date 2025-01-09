package com.example.domain.repository

import com.example.domain.model.UserDomainModel
import com.example.domain.network.ResultWrapper

interface UserRepository {
    suspend fun login(email: String, password: String): ResultWrapper<UserDomainModel>

    suspend fun register(
        email: String,
        password: String,
        name: String
    ): ResultWrapper<UserDomainModel>

    suspend fun changePassword(email: String, oldPassword: String, newPassword: String)

    suspend fun forgotPassword(email: String): ResultWrapper<Unit>
    suspend fun editProfile(email: String, name: String, avatarUrl: String): ResultWrapper<Unit>
}
