package com.example.e_commercial.ui.feature.account.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.network.ResultWrapper
import com.example.domain.usecase.RegisterUseCase
import com.example.e_commercial.EcommercialSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun register(email: String, password: String, name: String) {
        if (!isValidEmail(email)) {
            _registerState.value = RegisterState.Error("Invalid email format")
            return
        }
        if (!isValidPassword(password)) {
            _registerState.value = RegisterState.Error("Password must be at least 8 characters long")
            return
        }
        if (name.isBlank()) {
            _registerState.value = RegisterState.Error("Name cannot be empty")
            return
        }

        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            when (val response = registerUseCase.execute(email, password, name)) {
                is ResultWrapper.Success -> {
                    val userDomainModel = response.value
                    if (userDomainModel != null) { // Ensure domain model is not null
                        EcommercialSession.storeUser(userDomainModel)
                        _registerState.value = RegisterState.Success()
                    } else {
                        _registerState.value = RegisterState.Error("Unexpected error: Missing user data.")
                    }
                }
                is ResultWrapper.Failure -> {
                    _registerState.value = RegisterState.Error(
                        response.exception.message ?: "Something went wrong!"
                    )
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
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    class Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}
