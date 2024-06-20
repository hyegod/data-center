package com.next.up.code.core.data.api

import com.inyongtisto.myhelper.extension.getErrorBody
import com.next.up.code.core.data.api.network.ApiResponse
import com.next.up.code.core.data.api.network.ApiService
import com.next.up.code.core.data.api.network.ApiServiceIntegration
import com.next.up.code.core.data.api.response.ResponseData
import com.next.up.code.core.data.api.response.item.AnnouncementItem
import com.next.up.code.core.data.api.response.item.FileItem
import com.next.up.code.core.data.api.response.item.LoginItem
import com.next.up.code.core.data.api.response.item.NewsItem
import com.next.up.code.core.data.api.response.item.RoleItem
import com.next.up.code.core.data.api.response.item.UploadItem
import com.next.up.code.core.data.api.response.item.UserItem
import com.next.up.code.core.data.api.response.request.UpdatePasswordRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ApiDataSource(
    private val apiService: ApiService, private val apiServiceIntegration: ApiServiceIntegration
) {
    fun login(email: String, password: String): Flow<ApiResponse<LoginItem?>> {
        return flow {
            emit(ApiResponse.Empty)
            try {
                val response = apiService.login(email, password)
                if (response.isSuccessful) {
                    val body = response.body()
                    val data = body?.data
                    emit(ApiResponse.Success(data))
                } else {
                    emit(
                        ApiResponse.Error(
                            response.getErrorBody(ResponseData::class.java)?.message ?: ""
                        )
                    )
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getListFile(token: String): Flow<ApiResponse<List<FileItem>>> {
        return flow {
            try {
                val response = apiService.getListFile(token)
                val data = response.data
                if (data.isNotEmpty()) {
                    emit(ApiResponse.Success(data))
                } else {
                    emit(ApiResponse.Empty)
                }

            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)

    }

    fun uploadFolder(
        token: String, folderName: String, parentNameId: Int? = null
    ): Flow<ApiResponse<UploadItem?>> {
        return flow {
            emit(ApiResponse.Empty)
            try {
                val response = apiService.postFolder(token, folderName, parentNameId)
                if (response.isSuccessful) {
                    val body = response.body()
                    val data = body?.data
                    emit(ApiResponse.Success(data))
                } else {
                    emit(
                        ApiResponse.Error(
                            response.getErrorBody(ResponseData::class.java)?.message ?: ""
                        )
                    )
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun deleteFolder(
        token: String, folderId: Int, isRecycle: Int? = null
    ): Flow<ApiResponse<UploadItem?>> {
        return flow {
            emit(ApiResponse.Empty)
            try {
                val response = apiService.deleteFolder(token, folderId, isRecycle)
                if (response.isSuccessful) {
                    val body = response.body()
                    val data = body?.data
                    emit(ApiResponse.Success(data))
                } else {
                    emit(
                        ApiResponse.Error(
                            response.getErrorBody(ResponseData::class.java)?.message ?: ""
                        )
                    )
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun renameFolder(
        token: String, folderId: Int, folderName: String? = null
    ): Flow<ApiResponse<UploadItem?>> {
        return flow {
            emit(ApiResponse.Empty)
            try {
                val response = apiService.renameFolder(token, folderId, folderName)
                if (response.isSuccessful) {
                    val body = response.body()
                    val data = body?.data
                    emit(ApiResponse.Success(data))
                } else {
                    emit(
                        ApiResponse.Error(
                            response.getErrorBody(ResponseData::class.java)?.message ?: ""
                        )
                    )
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun insertLogDownload(
        token: String, folderId: Int
    ): Flow<ApiResponse<UploadItem?>> {
        return flow {
            emit(ApiResponse.Empty)
            try {
                val response = apiService.insertLogDownload(token, folderId)
                if (response.isSuccessful) {
                    val body = response.body()
                    val data = body?.data
                    emit(ApiResponse.Success(data))
                } else {
                    emit(
                        ApiResponse.Error(
                            response.getErrorBody(ResponseData::class.java)?.message ?: ""
                        )
                    )
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun uploadFile(
        token: String, folderName: MultipartBody.Part, parentNameId: RequestBody? = null
    ): Flow<ApiResponse<UploadItem?>> {
        return flow {
            emit(ApiResponse.Empty)
            try {
                val response = apiService.postFile(token, folderName, parentNameId)
                if (response.isSuccessful) {
                    val body = response.body()
                    val data = body?.data
                    emit(ApiResponse.Success(data))
                } else {
                    emit(
                        ApiResponse.Error(
                            response.getErrorBody(ResponseData::class.java)?.message ?: ""
                        )
                    )
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun updateProfile(
        token: String,
        picture: MultipartBody.Part?,
        method: RequestBody,
        personResponsible: RequestBody,
        nip: RequestBody
    ): Flow<ApiResponse<UserItem?>> {
        return flow {
            emit(ApiResponse.Empty)
            try {
                val response =
                    apiService.updateProfile(token, picture, method, personResponsible, nip)
                if (response.isSuccessful) {
                    val body = response.body()
                    val data = body?.data
                    emit(ApiResponse.Success(data))
                } else {
                    emit(
                        ApiResponse.Error(
                            response.getErrorBody(ResponseData::class.java)?.message ?: ""
                        )
                    )
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }


    fun getUser(token: String): Flow<ApiResponse<UserItem>> {
        return flow {
            try {
                val response = apiService.getUser(token)
                val data = response.data
                if (data != null) {
                    emit(ApiResponse.Success(data))
                } else {
                    emit(ApiResponse.Empty)
                }

            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)

    }

    fun getAnnouncement(token: String): Flow<ApiResponse<AnnouncementItem>> {
        return flow {
            try {
                val response = apiService.getAnnouncement(token)
                val data = response.data
                if (data != null) {
                    emit(ApiResponse.Success(data))
                } else {
                    emit(ApiResponse.Empty)
                }

            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)

    }

    fun getRole(token: String): Flow<ApiResponse<List<RoleItem>>> {
        return flow {
            try {
                val response = apiService.getRole(token)
                val data = response.data
                if (data != null) {
                    emit(ApiResponse.Success(data))
                } else {
                    emit(ApiResponse.Empty)
                }

            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)

    }


    fun getNews(): Flow<ApiResponse<List<NewsItem>>> {
        return flow {
            try {
                val response = apiServiceIntegration.getNews()
                val data = response.data.data
                if (data != null) {
                    emit(ApiResponse.Success(data))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun updatePassword(
        token: String, request: UpdatePasswordRequest
    ): Flow<ApiResponse<UserItem?>> {
        return flow {
            emit(ApiResponse.Empty)
            try {
                val response = apiService.updatePassword(token, request)
                if (response.isSuccessful) {
                    val body = response.body()
                    val data = body?.data
                    emit(ApiResponse.Success(data))
                } else {
                    emit(
                        ApiResponse.Error(
                            response.getErrorBody(ResponseData::class.java)?.message ?: ""
                        )
                    )
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }
}