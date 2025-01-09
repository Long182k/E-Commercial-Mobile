package com.example.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    val email: String,
    val oldPassword: String,
    val newPassword: String
)
