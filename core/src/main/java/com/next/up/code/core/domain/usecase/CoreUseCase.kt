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
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface CoreUseCase {
    fun login(email: String, password: String): Flow<Resource<Login>>
    fun updatePassword(token: String, request: UpdatePasswordRequest): Flow<Resource<User>>
    fun getListFile(token: String): Flow<Resource<List<LatestFile>>>
    fun getNews(): Flow<Resource<List<News>>>
    fun getRoles(token: String): Flow<Resource<List<Roles>>>
    fun getAnnouncement(token: String): Flow<Resource<Announcement>>
    fun getUser(): Flow<User>
    suspend fun deleteAllUser()
    suspend fun deleteAllFiles()
    fun setNews(): Flow<List<News>>
    fun uploadFolder(
        token: String,
        folderName: String,
        parentNameId: Int? = null
    ): Flow<Resource<Upload>>

    fun deleteFolder(
        token: String,
        folderId: Int,
        isRecycle: Int? = null
    ): Flow<Resource<Upload>>

    fun renameFolder(
        token: String,
        folderId: Int,
        folderName: String? = null
    ): Flow<Resource<Upload>>

    fun insertLogDownload(
        token: String,
        folderId: Int
    ): Flow<Resource<Upload>>

    fun uploadFile(
        token: String,
        file: MultipartBody.Part,
        parentNameId: RequestBody? = null
    ): Flow<Resource<Upload>>

    fun updateProfile(
        token: String,
        picture: MultipartBody.Part? = null,
        method: RequestBody,
        personResponsible: RequestBody,
        nip: RequestBody
    ): Flow<Resource<User>>


    fun getAllFolder(
        sortType: SortType,
        parentNameId: Int?,
        roleId: Int? = null
    ): Flow<List<LatestFile>>

    fun searchFilesByName(query: String): Flow<List<LatestFile>>


    fun insertUserToDB(token: String): Flow<Resource<User>>


    /* BreadCrumbs*/
    fun insertBreadCrumbs(breadCrumbs: BreadCrumbs)
    fun deleteLastRecord()

    fun deleteBreadCrumbs(breadCrumbs: BreadCrumbs)
    fun getAllBreadCrumbs(): Flow<List<BreadCrumbs>>
}