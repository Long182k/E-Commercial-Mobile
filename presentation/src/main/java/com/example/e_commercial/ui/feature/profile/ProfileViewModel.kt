package com.example.e_commercial.ui.feature.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.UserDomainModel
import com.example.domain.network.ResultWrapper
import com.example.domain.usecase.ChangePasswordUseCase
import com.example.e_commercial.EcommercialSession
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.example.domain.usecase.EditProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel(), KoinComponent {
    private val editProfileUseCase: EditProfileUseCase by inject()
    private val changePasswordUseCase: ChangePasswordUseCase by inject()
    private val session: EcommercialSession by inject()

    private val _user = MutableLiveData<UserDomainModel>()
    val user: LiveData<UserDomainModel> get() = _user

    private val _changePasswordState = MutableLiveData<Result<Unit>>()
    val changePasswordState: LiveData<Result<Unit>> get() = _changePasswordState

    init {
        _user.value = session.getUser()
    }

    private val _state = MutableStateFlow<ProfileEvent>(ProfileEvent.Nothing)
    val state = _state.asStateFlow()
    val userDomainModel = session.getUser()

    fun logout() {
        session.clearUser()
        _state.value = ProfileEvent.Success("Logged out successfully")
    }

    fun changePassword(email: String, oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            _state.value = ProfileEvent.Loading
            when (val result = changePasswordUseCase(email, oldPassword, newPassword)) {
                is ResultWrapper.Success -> {
                    _state.value = ProfileEvent.Success("Password changed successfully")
                }
                is ResultWrapper.Failure -> {
                    _state.value = ProfileEvent.Error("Failed to change password")
                }
            }
        }
    }

    fun editProfile(email: String, name: String, avatarUrl: String) {
        viewModelScope.launch {
            _state.value = ProfileEvent.Loading
            when (val result = editProfileUseCase.execute(email, name, avatarUrl)) {
                is ResultWrapper.Success -> {
                    userDomainModel?.let { currentUser ->
                        val updatedUser = currentUser.copy(
                            name = name,
                            avatarUrl = avatarUrl
                        )
                        session.storeUser(updatedUser)
                    }
                    _state.value = ProfileEvent.Success("Profile updated successfully")
                }
                is ResultWrapper.Failure -> {
                    _state.value = ProfileEvent.Error("Failed to update profile")
                }
            }
        }
    }

    fun resetChangePasswordState() {
        _changePasswordState.value = null
    }

}

sealed class ProfileEvent {
    data object Loading : ProfileEvent()
    data object Nothing : ProfileEvent()
    data class Success(val message: String) : ProfileEvent()
    data class Error(val message: String) : ProfileEvent()
}