package com.example.e_commercial.ui.feature.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.domain.model.UserDomainModel
import com.example.e_commercial.EcommercialSession

class ProfileViewModel : ViewModel() {
    private val _user = MutableLiveData<UserDomainModel>()
    val user: LiveData<UserDomainModel> get() = _user

    init {
        // Giả sử bạn có một hàm để lấy thông tin người dùng hiện tại
        _user.value = getCurrentUser()
    }

    private fun getCurrentUser(): UserDomainModel {
        // Giả sử bạn có một hàm để lấy thông tin người dùng hiện tại từ session hoặc database
        return EcommercialSession.getUser() ?: UserDomainModel(0, "", "", "")
    }
}
