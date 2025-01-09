package com.example.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Int?,
    val username: String,
    val email: String,
    val name: String,
    val avatarUrl: String? = null
) {
    fun toDomainModel() = com.example.domain.model.UserDomainModel(
        id = id,
        username = username,
        email = email,
        name = name,
        avatarUrl = avatarUrl
    )
}