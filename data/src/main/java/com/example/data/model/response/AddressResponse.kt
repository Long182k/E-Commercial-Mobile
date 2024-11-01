package com.example.data.model.response

import com.example.domain.model.AddressDomainModel
import kotlinx.serialization.Serializable

@Serializable
data class AddressResponse(
    val addressLine: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: String
) {
    fun toDomain(): AddressDomainModel {
        return AddressDomainModel(
            addressLine = addressLine,
            city = city,
            state = state,
            postalCode = postalCode,
            country = country
        )
    }
}
