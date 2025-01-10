package com.example.data.model.response

import com.example.domain.model.ProfileFormData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EditProfileResponse(
    val msg: String,
    val data: UserData
) {
    @Serializable
    data class UserData(
        val id: Long,
        val username: String,
        val email: String,
        val name: String,
        @SerialName("avatarUrl")
        val avatarUrl: String?
    )

    fun toDomainModel(): ProfileFormData {
        return ProfileFormData(
            email = data.email,
            name = data.name,
            avatarUrl = data.avatarUrl,
            avatarFile = null
        )
    }
}