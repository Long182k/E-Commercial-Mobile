package com.example.e_commercial.ui.feature.account.register

import android.net.http.NetworkException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.UserDomainModel
import com.example.domain.network.ResultWrapper
import com.example.domain.usecase.RegisterUseCase
import com.example.e_commercial.EcommercialSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
    private val ecommercialSession: EcommercialSession
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun updateName(newName: String) {
        _name.value = newName
    }

    fun register() {
        val currentEmail = _email.value
        val currentPassword = _password.value
        val currentName = _name.value

        if (!isValidEmail(currentEmail)) {
            _registerState.value = RegisterState.Error("Invalid email format")
            return
        }
        if (!isValidPassword(currentPassword)) {
            _registerState.value = RegisterState.Error("Password must be at least 8 characters long and include letters and numbers")
            return
        }
        if (currentName.isBlank()) {
            _registerState.value = RegisterState.Error("Name cannot be empty")
            return
        }

        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            try {
                when (val response = registerUseCase.execute(currentEmail, currentPassword, currentName)) {
                    is ResultWrapper.Success -> handleSuccess(response.value)
                    is ResultWrapper.Failure -> handleFailure(response)
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Unexpected error: ${e.message}")
            }
        }
    }

    private fun handleSuccess(userDomainModel: UserDomainModel?) {
        if (userDomainModel != null) {
            ecommercialSession.storeUser(userDomainModel)
            _registerState.value = RegisterState.Success()
        } else {
            _registerState.value = RegisterState.Error("Unexpected error: Missing user data.")
        }
    }

    private fun handleFailure(response: ResultWrapper.Failure) {
        val errorMessage = when (response.exception) {
            is NetworkException -> "Network error: Please check your internet connection."
            else -> response.exception.message ?: "An unknown error occurred."
        }
        _registerState.value = RegisterState.Error(errorMessage)
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return email.matches(emailRegex)
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8 && password.any { it.isDigit() } && password.any { it.isLetter() }
    }
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    class Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}
