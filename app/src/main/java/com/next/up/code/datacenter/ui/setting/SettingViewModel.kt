package com.next.up.code.datacenter.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.next.up.code.core.data.api.response.request.UpdatePasswordRequest
import com.next.up.code.core.domain.usecase.CoreUseCase
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SettingViewModel(private val coreUseCase: CoreUseCase) : ViewModel() {
    fun getUser() = coreUseCase.getUser().asLiveData()
    fun updatePassword(token: String, request: UpdatePasswordRequest) =
        coreUseCase.updatePassword(token, request).asLiveData()

    fun insertUserToDB(token: String) = coreUseCase.insertUserToDB(token).asLiveData()

    fun updateProfile(
        token: String,
        picture: MultipartBody.Part? = null,
        method: RequestBody,
        personResponsible: RequestBody,
        nip: RequestBody
    ) = coreUseCase.updateProfile(token, picture, method, personResponsible, nip).asLiveData()

    suspend fun deleteAllUser() = coreUseCase.deleteAllUser()
    suspend fun deleteAllFiles() = coreUseCase.deleteAllFiles()
}