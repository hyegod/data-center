package com.next.up.code.core.data

import com.next.up.code.core.data.api.ApiDataSource
import com.next.up.code.core.data.api.network.ApiResponse
import com.next.up.code.core.data.api.response.item.FileItem
import com.next.up.code.core.data.api.response.item.NewsItem
import com.next.up.code.core.data.api.response.request.UpdatePasswordRequest
import com.next.up.code.core.data.local.LocalDataSource
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
import com.next.up.code.core.utils.AppExecutors
import com.next.up.code.core.utils.DataMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CoreRepository(
    private val apiDataSource: ApiDataSource,
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors
) : ICoreRepository {
    override fun login(email: String, password: String): Flow<Resource<Login>> {
        return apiDataSource.login(email, password).map { response ->
            when (response) {
                is ApiResponse.Success -> Resource.Success(
                    DataMapper.loginItemToLogin(response.data!!)
                )

                is ApiResponse.Error -> Resource.Error(response.errorMessage)

                is ApiResponse.Empty -> Resource.Loading()
            }
        }
    }

    override fun getAnnouncement(token: String): Flow<Resource<Announcement>> {

        return apiDataSource.getAnnouncement(token).map { response ->
            when (response) {
                is ApiResponse.Success -> Resource.Success(
                    Announcement(
                        response.data.id,
                        response.data.title,
                        response.data.users.personResponsible.toString(),
                        response.data.date,
                        response.data.file
                    )
                )

                is ApiResponse.Empty -> Resource.Loading()
                is ApiResponse.Error -> Resource.Error(response.errorMessage)
            }
        }
    }

    override fun getRoles(token: String): Flow<Resource<List<Roles>>> {
        return apiDataSource.getRole(token).map { response ->
            when (response) {
                is ApiResponse.Success -> Resource.Success(
                    DataMapper.roleItemToRole(response.data)
                )

                is ApiResponse.Error -> Resource.Error(response.errorMessage)

                is ApiResponse.Empty -> Resource.Loading()
            }
        }
    }

    override fun getListFile(token: String): Flow<Resource<List<LatestFile>>> =
        object : NetworkBoundResource<List<LatestFile>, List<FileItem>>() {
            override fun loadFromDB(): Flow<List<LatestFile>> {
                return localDataSource.getAllList().map {
                    DataMapper.fileEntityToLatestFile(it)
                }
            }

            override suspend fun createCall(): Flow<ApiResponse<List<FileItem>>> {
                return apiDataSource.getListFile(token)
            }

            override suspend fun saveCallResult(data: List<FileItem>) {
                val fileList = DataMapper.fileItemToFileEntity(data)
                withContext(Dispatchers.IO) {
                    localDataSource.insertFile(fileList)
                }
            }

            override fun shouldFetch(data: List<LatestFile>?): Boolean {
                if (data.isNullOrEmpty()) {
                    return true
                }
                return true
            }

        }.asFlow()


    override fun insertUserToDB(token: String): Flow<Resource<User>> {
        return apiDataSource.getUser(token).map { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Success -> {
                    val userEntity = DataMapper.userItemToUserEntity(apiResponse.data)
                    withContext(Dispatchers.IO) {
                        localDataSource.insertUser(userEntity)
                    }
                    Resource.Success(
                        User(
                            apiResponse.data.id,
                            apiResponse.data.name,
                            apiResponse.data.email,
                            apiResponse.data.picture,
                            apiResponse.data.personResponsible,
                            apiResponse.data.nip,
                            apiResponse.data.roles,
                            apiResponse.data.permission_edit,
                            apiResponse.data.permission_delete,
                            apiResponse.data.permission_upload,
                            apiResponse.data.permission_create,
                            apiResponse.data.permission_download,
                        )
                    )
                }

                is ApiResponse.Error -> Resource.Error(apiResponse.errorMessage)
                is ApiResponse.Empty -> Resource.Loading()
            }
        }
    }

    override fun insertBreadCrumbs(breadCrumbs: BreadCrumbs) {
        val breadCrumbsEntity = DataMapper.breadCrumbsToBreadCrumbsEntity(breadCrumbs)
        appExecutors.diskIO().execute {
            localDataSource.insertBreadCrumbs(breadCrumbsEntity)
        }
    }

    override fun deleteLastRecord() {
        return localDataSource.deleteLastRecord()
    }

    override fun deleteBreadCrumbs(breadCrumbs: BreadCrumbs) {
        val breadCrumbsEntity = DataMapper.breadCrumbsToBreadCrumbsEntity(breadCrumbs)
        appExecutors.diskIO().execute {
            localDataSource.deleteBreadCrumbs(breadCrumbsEntity)
        }
    }

    override fun getAllBreadCrumbs(): Flow<List<BreadCrumbs>> {
        return localDataSource.getAllBreadCrumbs().map {
            DataMapper.breadCrumbsEntityToBreadCrumbs(it)
        }
    }


    override fun uploadFolder(
        token: String, folderName: String, parentNameId: Int?
    ): Flow<Resource<Upload>> {
        return apiDataSource.uploadFolder(token, folderName, parentNameId).map { response ->
            when (response) {
                is ApiResponse.Success -> Resource.Success(
                    Upload(
                        response.data!!.id, response.data.folderName
                    )

                )

                is ApiResponse.Error -> Resource.Error(response.errorMessage)

                is ApiResponse.Empty -> Resource.Loading()
            }
        }
    }

    override fun deleteFolder(
        token: String, folderId: Int, isRecycle: Int?
    ): Flow<Resource<Upload>> {
        return apiDataSource.deleteFolder(token, folderId, isRecycle).map { response ->
            when (response) {
                is ApiResponse.Success -> Resource.Success(
                    Upload(
                        response.data!!.id, response.data.folderName
                    )

                )

                is ApiResponse.Error -> Resource.Error(response.errorMessage)

                is ApiResponse.Empty -> Resource.Loading()
            }
        }
    }


    override fun renameFolder(
        token: String, folderId: Int, folderName: String?
    ): Flow<Resource<Upload>> {
        return apiDataSource.renameFolder(token, folderId, folderName).map { response ->
            when (response) {
                is ApiResponse.Success -> Resource.Success(
                    Upload(
                        response.data!!.id, response.data.folderName
                    )

                )

                is ApiResponse.Error -> Resource.Error(response.errorMessage)

                is ApiResponse.Empty -> Resource.Loading()
            }
        }
    }


    override fun insertLogDownload(
        token: String, folderId: Int
    ): Flow<Resource<Upload>> {
        return apiDataSource.insertLogDownload(token, folderId).map { response ->
            when (response) {
                is ApiResponse.Success -> Resource.Success(
                    Upload(
                        response.data!!.id, response.data.folderName
                    )

                )

                is ApiResponse.Error -> Resource.Error(response.errorMessage)

                is ApiResponse.Empty -> Resource.Loading()
            }
        }
    }

    override fun uploadFile(
        token: String, file: MultipartBody.Part, parentNameId: RequestBody?
    ): Flow<Resource<Upload>> {
        return apiDataSource.uploadFile(token, file, parentNameId).map { response ->
            when (response) {

                is ApiResponse.Success -> Resource.Success(
                    Upload(
                        response.data!!.id, response.data.folderName
                    )

                )

                is ApiResponse.Error -> Resource.Error(response.errorMessage)

                is ApiResponse.Empty -> Resource.Loading()
            }
        }
    }

    override fun updateProfile(
        token: String,
        picture: MultipartBody.Part?,
        method: RequestBody,
        personResponsible: RequestBody,
        nip: RequestBody
    ): Flow<Resource<User>> {
        return apiDataSource.updateProfile(token, picture, method, personResponsible, nip)
            .map { response ->
                when (response) {
                    is ApiResponse.Success -> Resource.Success(
                        User(
                            response.data?.id,
                            response.data?.name,
                            response.data?.email,
                            response.data?.picture,
                            response.data?.personResponsible,
                            response.data?.nip,
                            response.data?.roles,
                            response.data?.permission_edit,
                            response.data?.permission_delete,
                            response.data?.permission_upload,
                            response.data?.permission_create,
                            response.data?.permission_download,
                        )

                    )

                    is ApiResponse.Error -> Resource.Error(response.errorMessage)

                    is ApiResponse.Empty -> Resource.Loading()
                }
            }
    }

    override fun getAllFolder(
        sortType: SortType,
        parentNameId: Int?,
        roleId: Int?
    ): Flow<List<LatestFile>> {
        return localDataSource.getAllFolder(sortType, parentNameId, roleId).map {
            DataMapper.fileEntityToLatestFile(it)
        }
    }


    override fun searchFilesByName(query: String): Flow<List<LatestFile>> {
        return localDataSource.searchFilesByName(query).map {
            DataMapper.fileEntityToLatestFile(it)
        }
    }


    override fun getUser(): Flow<User> {
        return localDataSource.setUser().map {
            DataMapper.userEntityToUser(it)
        }
    }

    override suspend fun deleteAllUser() {
        withContext(Dispatchers.IO) {
            localDataSource.deleteAllUsers()
        }
    }


    override suspend fun deleteAllFiles() {
        withContext(Dispatchers.IO) {
            localDataSource.deleteAllFiles()
        }
    }

    override fun getNews(): Flow<Resource<List<News>>> =
        object : NetworkBoundResource<List<News>, List<NewsItem>>() {
            override fun loadFromDB(): Flow<List<News>> {
                return localDataSource.getNews().map {
                    DataMapper.newsEntityToNews(it)
                }
            }

            override suspend fun createCall(): Flow<ApiResponse<List<NewsItem>>> {
                return apiDataSource.getNews()
            }

            override suspend fun saveCallResult(data: List<NewsItem>) {
                val newsList = DataMapper.newsItemToNewsEntity(data)
                withContext(Dispatchers.IO) {
                    localDataSource.insertNews(newsList)
                }
            }

            override fun shouldFetch(data: List<News>?): Boolean {
                return true
            }

        }.asFlow()

    override fun setNews(): Flow<List<News>> {
        return localDataSource.getNewsLatest().map {
            DataMapper.newsEntityToNews(it)
        }
    }

    override fun updatePassword(
        token: String,
        request: UpdatePasswordRequest
    ): Flow<Resource<User>> {
        return apiDataSource.updatePassword(token, request).map { response ->
            when (response) {
                is ApiResponse.Success -> Resource.Success(
                    User(
                        response.data?.id,
                        response.data?.name,
                        response.data?.email,
                        response.data?.picture,
                        response.data?.personResponsible,
                        response.data?.nip,
                        response.data?.roles,
                        response.data?.permission_edit,
                        response.data?.permission_delete,
                        response.data?.permission_upload,
                        response.data?.permission_create,
                        response.data?.permission_download,

                        )
                )

                is ApiResponse.Error -> Resource.Error(response.errorMessage)

                is ApiResponse.Empty -> Resource.Loading()
            }
        }
    }


    /* BreadCrumbs*/


}