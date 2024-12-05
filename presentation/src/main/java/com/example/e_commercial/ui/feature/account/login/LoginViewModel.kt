package com.example.e_commercial.ui.feature.account.login


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.network.ResultWrapper
import com.example.domain.usecase.LoginUseCase
import com.example.e_commercial.EcommercialSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        if (!isValidEmail(email)) {
            _loginState.value = LoginState.Error("Invalid email format") // Trigger error state
            return
        }
        if (!isValidPassword(password)) {
            _loginState.value = LoginState.Error("Password must be at least 8 characters long") // Trigger error state
            return
        }

        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            val response = loginUseCase.execute(email, password)
            when (response) {
                is ResultWrapper.Success -> {
                    val userDomainModel = response.value
                    if (userDomainModel != null) { // Ensure the domain model is not null
                        EcommercialSession.storeUser(userDomainModel) // Use the domain model
                        _loginState.value = LoginState.Success()
                    } else {
                        _loginState.value = LoginState.Error("Unexpected error: Missing user data.")
                    }
                }
                is ResultWrapper.Failure -> {
                    _loginState.value = LoginState.Error(response.exception.message ?: "Something went wrong!")
                }
            }
        }
    }


    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return email.matches(emailRegex)
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    class Success : LoginState()
    data class Error(val message: String) : LoginState()
}
