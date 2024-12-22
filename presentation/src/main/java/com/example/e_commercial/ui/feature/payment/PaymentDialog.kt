package com.example.e_commercial.ui.feature.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.e_commercial.ui.feature.account.login.PurpleButton

@Composable
fun PaymentDialog(
    onDismiss: () -> Unit,
    onConfirmPayment: (String, String, String) -> Unit
) {
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    var cardNumberError by remember { mutableStateOf("") }
    var expiryDateError by remember { mutableStateOf("") }
    var cvvError by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Payment Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = {
                        cardNumber = it
                        cardNumberError = validateCardNumber(it)
                    },
                    label = { Text("Card Number") },
                    isError = cardNumberError.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (cardNumberError.isNotEmpty()) {
                    Text(
                        text = cardNumberError,
                        color = MaterialTheme.colorScheme.error, // Error color from the theme
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = {
                        expiryDate = it
                        expiryDateError = validateExpiryDate(it)
                    },
                    label = { Text("Expiry Date (MM/YY)") },
                    isError = expiryDateError.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (expiryDateError.isNotEmpty()) {
                    Text(
                        text = expiryDateError,
                        color = MaterialTheme.colorScheme.error, // Error color from the theme
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                OutlinedTextField(
                    value = cvv,
                    onValueChange = {
                        cvv = it
                        cvvError = validateCVV(it)
                    },
                    label = { Text("CVV") },
                    isError = cvvError.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (cvvError.isNotEmpty()) {
                    Text(
                        text = cvvError,
                        color = MaterialTheme.colorScheme.error, // Error color from the theme
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary // Primary color for the text button
                        )
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (cardNumberError.isEmpty() &&
                                expiryDateError.isEmpty() &&
                                cvvError.isEmpty()
                            ) {
                                onConfirmPayment(cardNumber, expiryDate, cvv)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary, // Primary color for the button
                            contentColor = MaterialTheme.colorScheme.onPrimary // Text color on primary background
                        ),
                        enabled = cardNumberError.isEmpty() &&
                                expiryDateError.isEmpty() &&
                                cvvError.isEmpty()
                    ) {
                        Text("Pay Now")
                    }
                }
            }
        }
    }
}


fun validateCardNumber(cardNumber: String): String {
    return when {
        cardNumber.isBlank() -> "Card number cannot be empty"
        cardNumber.length != 16 -> "Card number must be 16 digits"
        !cardNumber.all { it.isDigit() } -> "Card number must contain only digits"
        else -> ""
    }
}

fun validateExpiryDate(expiryDate: String): String {
    val regex = Regex("""^(0[1-9]|1[0-2])\/\d{2}$""")
    return when {
        expiryDate.isBlank() -> "Expiry date cannot be empty"
        !regex.matches(expiryDate) -> "Expiry date must be in MM/YY format"
        else -> {
            val (month, year) = expiryDate.split("/").map { it.toInt() }
            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100
            val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1
            when {
                year < currentYear || (year == currentYear && month < currentMonth) -> "Expiry date cannot be in the past"
                else -> ""
            }
        }
    }
}

fun validateCVV(cvv: String): String {
    return when {
        cvv.isBlank() -> "CVV cannot be empty"
        cvv.length != 3 -> "CVV must be 3 digits"
        !cvv.all { it.isDigit() } -> "CVV must contain only digits"
        else -> ""
    }
}

