package com.next.up.code.datacenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.next.up.code.core.domain.usecase.CoreUseCase
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val coreUseCase: CoreUseCase) : ViewModel() {
    fun uploadFolder(
        token: String,
        folderName: String,
        parentNameId: Int? = null
    ) = coreUseCase.uploadFolder(token, folderName, parentNameId).asLiveData()


    fun uploadFile(
        token: String,
        file: MultipartBody.Part,
        parentNameId: RequestBody? = null
    ) = coreUseCase.uploadFile(token, file, parentNameId).asLiveData()
}