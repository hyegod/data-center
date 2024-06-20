package com.next.up.code.datacenter.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.next.up.code.core.domain.usecase.CoreUseCase

class LoginViewModel(private val useCase: CoreUseCase) : ViewModel() {
    fun login(email: String, password: String) = useCase.login(email, password).asLiveData()
}