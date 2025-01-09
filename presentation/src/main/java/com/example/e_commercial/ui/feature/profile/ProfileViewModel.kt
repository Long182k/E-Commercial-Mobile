package com.example.e_commercial.ui.feature.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.UserDomainModel
import com.example.domain.network.ResultWrapper
import com.example.domain.usecase.ChangePasswordUseCase
import com.example.domain.usecase.UpdateUserDetailsUseCase
import com.example.e_commercial.EcommercialSession
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProfileViewModel : ViewModel(), KoinComponent {
    private val session: EcommercialSession by inject()
    private val changePasswordUseCase: ChangePasswordUseCase by inject()
    private val updateUserDetailsUseCase: UpdateUserDetailsUseCase by inject()

    private val _user = MutableLiveData<UserDomainModel>()
    val user: LiveData<UserDomainModel> get() = _user

    private val _changePasswordState = MutableLiveData<Result<Unit>>()
    val changePasswordState: LiveData<Result<Unit>> get() = _changePasswordState

    init {
        _user.value = session.getUser()
    }

    fun logout() {
        session.clearUser()
    }

    fun changePassword(email: String, oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            val resultWrapper = changePasswordUseCase(email, oldPassword, newPassword)
            val result = when (resultWrapper) {
                is ResultWrapper.Success -> Result.success(Unit)
                is ResultWrapper.Failure -> Result.failure(resultWrapper.exception)
            }
            _changePasswordState.postValue(result)
        }
    }

    fun updateUserName(newName: String) {

    }
    fun updateUserAvatar(newAvatarUrl: String) {

    }
}


