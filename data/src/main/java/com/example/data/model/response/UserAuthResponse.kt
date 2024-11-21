package com.example.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class UserAuthResponse(
    val `data`: UserResponse? = null,
    val msg: String
)