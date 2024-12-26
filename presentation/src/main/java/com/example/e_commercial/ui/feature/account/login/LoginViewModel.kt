package com.example.e_commercial.ui.feature.account.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.UserDomainModel
import com.example.domain.network.ResultWrapper
import com.example.domain.usecase.LoginUseCase
import com.example.e_commercial.EcommercialSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val ecommercialSession: EcommercialSession
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    // State to hold user input
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun login() {
        val currentEmail = _email.value
        val currentPassword = _password.value

        // Validate email format
        if (!isValidEmail(currentEmail)) {
            _loginState.value = LoginState.Error("Invalid email format")
            return
        }

        // Validate password length
        if (!isValidPassword(currentPassword)) {
            _loginState.value = LoginState.Error("Password must be at least 8 characters long")
            return
        }

        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            when (val response = loginUseCase.execute(currentEmail, currentPassword)) {
                is ResultWrapper.Success -> handleSuccess(response.value)
                is ResultWrapper.Failure -> handleFailure(response)
            }
        }
    }

    private fun handleSuccess(userDomainModel: UserDomainModel?) {
        if (userDomainModel != null) {
            ecommercialSession.storeUser(userDomainModel)
            _loginState.value = LoginState.Success()
        } else {
            _loginState.value = LoginState.Error("Unexpected error: Missing user data.")
        }
    }

    private fun handleFailure(response: ResultWrapper.Failure) {
        val errorMessage = response.exception.message ?: "Something went wrong!"
        _loginState.value = LoginState.Error(errorMessage)
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return email.matches(emailRegex)
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    class Success : LoginState()
    data class Error(val message: String) : LoginState()
}
