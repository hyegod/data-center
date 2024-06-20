package com.next.up.code.core.domain.usecase

import com.next.up.code.core.data.Resource
import com.next.up.code.core.data.api.response.request.UpdatePasswordRequest
import com.next.up.code.core.data.local.constants.SortType
import com.next.up.code.core.domain.model.Announcement
import com.next.up.code.core.domain.model.BreadCrumbs
import com.next.up.code.core.domain.model.LatestFile
import com.next.up.code.core.domain.model.Login
import com.next.up.code.core.domain.model.News
import com.next.up.code.core.domain.model.Roles
import com.next.up.code.core.domain.model.Upload
import com.next.up.code.core.domain.model.User
import com.next.up.code.core.domain.repository.ICoreRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CoreInteract(private val iCoreRepository: ICoreRepository) : CoreUseCase {
    override fun login(email: String, password: String): Flow<Resource<Login>> {
        return iCoreRepository.login(email, password)
    }

    override fun updatePassword(
        token: String,
        request: UpdatePasswordRequest
    ): Flow<Resource<User>> {
        return iCoreRepository.updatePassword(token, request)
    }

    override fun getListFile(token: String): Flow<Resource<List<LatestFile>>> {
        return iCoreRepository.getListFile(token)
    }

    override fun getNews(): Flow<Resource<List<News>>> {
        return iCoreRepository.getNews()
    }

    override fun getRoles(token: String): Flow<Resource<List<Roles>>> {
        return iCoreRepository.getRoles(token)
    }

    override fun getAnnouncement(token: String): Flow<Resource<Announcement>> {
        return iCoreRepository.getAnnouncement(token)
    }

    override fun getUser(): Flow<User> {
        return iCoreRepository.getUser()
    }

    override suspend fun deleteAllUser() {
        return iCoreRepository.deleteAllUser()
    }

    override suspend fun deleteAllFiles() {
        return iCoreRepository.deleteAllFiles()
    }

    override fun setNews(): Flow<List<News>> {

        return iCoreRepository.setNews()
    }

    override fun uploadFolder(
        token: String,
        folderName: String,
        parentNameId: Int?
    ): Flow<Resource<Upload>> {
        return iCoreRepository.uploadFolder(token, folderName, parentNameId)
    }

    override fun deleteFolder(
        token: String,
        folderId: Int,
        isRecycle: Int?
    ): Flow<Resource<Upload>> {
        return iCoreRepository.deleteFolder(token, folderId, isRecycle)
    }

    override fun renameFolder(
        token: String,
        folderId: Int,
        folderName: String?
    ): Flow<Resource<Upload>> {
        return iCoreRepository.renameFolder(token, folderId, folderName)
    }

    override fun insertLogDownload(token: String, folderId: Int): Flow<Resource<Upload>> {
        return iCoreRepository.insertLogDownload(token, folderId)
    }

    override fun uploadFile(
        token: String,
        file: MultipartBody.Part,
        parentNameId: RequestBody?
    ): Flow<Resource<Upload>> {
        return iCoreRepository.uploadFile(token, file, parentNameId)
    }

    override fun updateProfile(
        token: String,
        picture: MultipartBody.Part?,
        method: RequestBody,
        personResponsible: RequestBody,
        nip: RequestBody
    ): Flow<Resource<User>> {
        return iCoreRepository.updateProfile(token, picture, method, personResponsible, nip)
    }

    override fun getAllFolder(
        sortType: SortType,
        parentNameId: Int?,
        roleId: Int?
    ): Flow<List<LatestFile>> {
        return iCoreRepository.getAllFolder(sortType, parentNameId, roleId)
    }

    override fun searchFilesByName(query: String): Flow<List<LatestFile>> {
        return iCoreRepository.searchFilesByName(query)
    }

    override fun insertUserToDB(token: String): Flow<Resource<User>> {
        return iCoreRepository.insertUserToDB(token)
    }

    override fun insertBreadCrumbs(breadCrumbs: BreadCrumbs) {
        return iCoreRepository.insertBreadCrumbs(breadCrumbs)
    }

    override fun deleteLastRecord() {
        return iCoreRepository.deleteLastRecord()
    }

    override fun deleteBreadCrumbs(breadCrumbs: BreadCrumbs) {
        return iCoreRepository.deleteBreadCrumbs(breadCrumbs)
    }

    override fun getAllBreadCrumbs(): Flow<List<BreadCrumbs>> {
        return iCoreRepository.getAllBreadCrumbs()
    }
}