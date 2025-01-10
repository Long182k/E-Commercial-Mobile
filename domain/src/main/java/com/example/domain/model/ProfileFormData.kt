package com.example.domain.model

import java.io.File

data class ProfileFormData(
    val email: String,
    val name: String,
    val avatarFile: File?,
    val avatarUrl: String?,
) {
}