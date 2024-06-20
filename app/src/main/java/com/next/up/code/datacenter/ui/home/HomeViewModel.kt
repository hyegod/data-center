package com.next.up.code.datacenter.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.next.up.code.core.domain.usecase.CoreUseCase

class HomeViewModel(private val coreUseCase: CoreUseCase) : ViewModel() {
    fun getListFile(token: String) = coreUseCase.getListFile(token).asLiveData()

    fun getAnnouncement(token: String) = coreUseCase.getAnnouncement(token).asLiveData()

    fun insertUserToDB(token: String) = coreUseCase.insertUserToDB(token).asLiveData()

    fun getUser() = coreUseCase.getUser().asLiveData()

    fun getNews() = coreUseCase.getNews().asLiveData()
    fun setNews() = coreUseCase.setNews().asLiveData()
    fun deleteFolder(
        token: String,
        folderId: Int,
        isRecycle: Int? = null
    ) = coreUseCase.deleteFolder(token, folderId, isRecycle).asLiveData()

    fun renameFolder(
        token: String,
        folderId: Int,
        folderName: String? = null
    ) = coreUseCase.renameFolder(token, folderId, folderName).asLiveData()

    suspend fun deleteAllFiles() = coreUseCase.deleteAllFiles()
}