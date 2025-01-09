package com.example.e_commercial.ui.feature.account.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.network.ResultWrapper
import com.example.domain.usecase.ForgotPasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _forgotPasswordState = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val forgotPasswordState: StateFlow<ForgotPasswordState> = _forgotPasswordState

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
        if (_forgotPasswordState.value is ForgotPasswordState.Error) {
            _forgotPasswordState.value = ForgotPasswordState.Idle
        }
    }

    fun submitForgotPasswordRequest() {
        val currentEmail = _email.value.trim()

        if (!isValidEmail(currentEmail)) {
            _forgotPasswordState.value = ForgotPasswordState.Error("Invalid email format")
            return
        }

        _forgotPasswordState.value = ForgotPasswordState.Loading
        viewModelScope.launch {
            val result = forgotPasswordUseCase.execute(currentEmail)
            handleForgotPasswordResponse(result)
        }
    }

    private fun handleForgotPasswordResponse(result: ResultWrapper<Unit>) {
        when (result) {
            is ResultWrapper.Success -> {
                _forgotPasswordState.value = ForgotPasswordState.Success
            }
            is ResultWrapper.Failure -> {
                val errorMessage = result.exception.message ?: "An unexpected error occurred"
                _forgotPasswordState.value = ForgotPasswordState.Error(errorMessage)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return email.matches(emailRegex)
    }
}

sealed class ForgotPasswordState {
    object Idle : ForgotPasswordState()
    object Loading : ForgotPasswordState()
    object Success : ForgotPasswordState()
    data class Error(val message: String) : ForgotPasswordState()
}
