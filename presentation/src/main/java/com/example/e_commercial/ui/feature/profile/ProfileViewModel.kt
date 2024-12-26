package com.example.e_commercial.ui.feature.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.domain.model.UserDomainModel
import com.example.e_commercial.EcommercialSession
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProfileViewModel : ViewModel(), KoinComponent {
    private val session: EcommercialSession by inject()

    private val _user = MutableLiveData<UserDomainModel>()
    val user: LiveData<UserDomainModel> get() = _user

    init {
        _user.value = session.getUser()
    }

    fun logout() {
        session.clearUser()
    }
}
