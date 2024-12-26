package com.example.e_commercial.ui.feature.user_address


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.e_commercial.model.UserAddress

const val USER_ADDRESS_SCREEN = "user_address"

@Composable
fun UserAddressScreen(navController: NavController, userAddress: UserAddress?) {
    val addressLine = remember { mutableStateOf(userAddress?.addressLine ?: "") }
    val city = remember { mutableStateOf(userAddress?.city ?: "") }
    val state = remember { mutableStateOf(userAddress?.state ?: "") }
    val postalCode = remember { mutableStateOf(userAddress?.postalCode ?: "") }
    val country = remember { mutableStateOf(userAddress?.country ?: "") }

    // Error states for validation
    val addressLineError = remember { mutableStateOf("") }
    val cityError = remember { mutableStateOf("") }
    val stateError = remember { mutableStateOf("") }
    val postalCodeError = remember { mutableStateOf("") }
    val countryError = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = addressLine.value,
            onValueChange = {
                addressLine.value = it
                addressLineError.value = validateAddressLine(it)
            },
            label = { Text(text = "Address Line") },
            isError = addressLineError.value.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )
        if (addressLineError.value.isNotEmpty()) {
            Text(
                text = addressLineError.value,
                color = Color.Red,
                modifier = Modifier.padding(4.dp)
            )
        }

        OutlinedTextField(
            value = city.value,
            onValueChange = {
                city.value = it
                cityError.value = validateCity(it)
            },
            label = { Text(text = "City") },
            isError = cityError.value.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )
        if (cityError.value.isNotEmpty()) {
            Text(
                text = cityError.value,
                color = Color.Red,
                modifier = Modifier.padding(4.dp)
            )
        }

        OutlinedTextField(
            value = state.value,
            onValueChange = {
                state.value = it
                stateError.value = validateState(it)
            },
            label = { Text(text = "State") },
            isError = stateError.value.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )
        if (stateError.value.isNotEmpty()) {
            Text(
                text = stateError.value,
                color = Color.Red,
                modifier = Modifier.padding(4.dp)
            )
        }

        OutlinedTextField(
            value = postalCode.value,
            onValueChange = {
                postalCode.value = it
                postalCodeError.value = validatePostalCode(it)
            },
            label = { Text(text = "Postal Code") },
            isError = postalCodeError.value.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )
        if (postalCodeError.value.isNotEmpty()) {
            Text(
                text = postalCodeError.value,
                color = Color.Red,
                modifier = Modifier.padding(4.dp)
            )
        }

        OutlinedTextField(
            value = country.value,
            onValueChange = {
                country.value = it
                countryError.value = validateCountry(it)
            },
            label = { Text(text = "Country") },
            isError = countryError.value.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )
        if (countryError.value.isNotEmpty()) {
            Text(
                text = countryError.value,
                color = Color.Red,
                modifier = Modifier.padding(4.dp)
            )
        }

        Button(
            onClick = {
                val address = UserAddress(
                    addressLine = addressLine.value,
                    city = city.value,
                    state = state.value,
                    postalCode = postalCode.value,
                    country = country.value
                )
                val previousBackStack = navController.previousBackStackEntry
                previousBackStack?.savedStateHandle?.set(USER_ADDRESS_SCREEN, address)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = addressLineError.value.isEmpty() &&
                    cityError.value.isEmpty() &&
                    stateError.value.isEmpty() &&
                    postalCodeError.value.isEmpty() &&
                    countryError.value.isEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Updated to match the "Checkout" button
                contentColor = Color.White,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        ) {
            Text(text = "Save")
        }

    }
}

fun validateAddressLine(addressLine: String): String {
    return when {
        addressLine.isBlank() -> "Address Line cannot be empty"
        !addressLine.any { it.isLetter() } -> "Address Line must include at least one letter"
        !addressLine.any { it.isDigit() } -> "Address Line must include at least one number"
        else -> ""
    }
}


fun validateCity(city: String): String {
    return if (city.isBlank()) "City cannot be empty"
    else if (!city.all { it.isLetter() || it.isWhitespace() }) "City must contain only letters"
    else ""
}

fun validateState(state: String): String {
    return if (state.isBlank()) "State cannot be empty"
    else if (!state.all { it.isLetter() || it.isWhitespace() }) "State must contain only letters"
    else ""
}

fun validatePostalCode(postalCode: String): String {
    return if (postalCode.isBlank()) "Postal Code cannot be empty"
    else if (!postalCode.all { it.isDigit() }) "Postal Code must contain only numbers"
    else if (postalCode.length !in 4..10) "Postal Code must be 4-10 digits long"
    else ""
}

fun validateCountry(country: String): String {
    return if (country.isBlank()) "Country cannot be empty"
    else if (!country.all { it.isLetter() || it.isWhitespace() }) "Country must contain only letters"
    else ""
}
