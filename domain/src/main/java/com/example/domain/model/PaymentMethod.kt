    package com.example.domain.model

    data class PaymentMethod(
        val id: String,
        val name: String,
        val type: PaymentMethodType,
        val details: String = "" // Additional details if needed
    )

    enum class PaymentMethodType {
        Cash,
        Card
    }
