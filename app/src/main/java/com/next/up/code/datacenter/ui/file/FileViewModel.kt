package com.next.up.code.datacenter.ui.file

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.next.up.code.core.data.local.constants.SortType
import com.next.up.code.core.domain.usecase.CoreUseCase
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FileViewModel(private val coreUseCase: CoreUseCase) : ViewModel() {

    val getUser = coreUseCase.getUser().asLiveData()
    fun getRoles(token: String) = coreUseCase.getRoles(token).asLiveData()
    fun getAllFolder(sortType: SortType, parentNameId: Int?, roleId: Int? = null) =
        coreUseCase.getAllFolder(sortType, parentNameId, roleId).asLiveData()

    fun searchFilesByName(query: String) = coreUseCase.searchFilesByName("%$query%").asLiveData()
    fun deleteFolder(
        token: String,
        folderId: Int,
        isRecycle: Int? = null
    ) = coreUseCase.deleteFolder(token, folderId, isRecycle).asLiveData()

    fun getUser() = coreUseCase.getUser().asLiveData()

    fun renameFolder(
        token: String,
        folderId: Int,
        folderName: String? = null
    ) = coreUseCase.renameFolder(token, folderId, folderName).asLiveData()


    suspend fun deleteAllFiles() = coreUseCase.deleteAllFiles()

    fun getListFile(token: String) = coreUseCase.getListFile(token).asLiveData()

    fun insertLogDownload(token: String, folderId: Int) =
        coreUseCase.insertLogDownload(token, folderId).asLiveData()


    fun uploadFile(
        token: String,
        file: MultipartBody.Part,
        parentNameId: RequestBody? = null
    ) = coreUseCase.uploadFile(token, file, parentNameId).asLiveData()
}