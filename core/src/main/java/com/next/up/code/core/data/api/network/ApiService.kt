package com.next.up.code.core.data.api.network

import com.next.up.code.core.data.api.response.ResponseData
import com.next.up.code.core.data.api.response.item.AnnouncementItem
import com.next.up.code.core.data.api.response.item.FileItem
import com.next.up.code.core.data.api.response.item.LoginItem
import com.next.up.code.core.data.api.response.item.RoleItem
import com.next.up.code.core.data.api.response.item.UploadItem
import com.next.up.code.core.data.api.response.item.UserItem
import com.next.up.code.core.data.api.response.request.UpdatePasswordRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("v1/login")
    suspend fun login(
        @Field("email") email: String, @Field("password") password: String
    ): Response<ResponseData<LoginItem>>

    @GET("v1/list-data")
    suspend fun getListFile(@Header("Authorization") token: String): ResponseData<List<FileItem>>

    @FormUrlEncoded
    @POST("v1/upload-folder")
    suspend fun postFolder(
        @Header("Authorization") token: String,
        @Field("folder_name") folderName: String,
        @Field("parent_name_id") parentNameId: Int? = null
    ): Response<ResponseData<UploadItem>>

    @FormUrlEncoded
    @PUT("v1/update-file/{id}")
    suspend fun deleteFolder(
        @Header("Authorization") token: String,
        @Path("id") folderId: Int,
        @Field("is_recycle") isRecycle: Int? = null
    ): Response<ResponseData<UploadItem>>

    @FormUrlEncoded
    @PUT("v1/update-folder/{id}")
    suspend fun renameFolder(
        @Header("Authorization") token: String,
        @Path("id") folderId: Int,
        @Field("folder_name") folderName: String? = null
    ): Response<ResponseData<UploadItem>>

    @Multipart
    @POST("v1/upload-file")
    suspend fun postFile(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("parent_name_id") parentNameId: RequestBody? = null
    ): Response<ResponseData<UploadItem>>

    @GET("v1/details-user")
    suspend fun getUser(@Header("Authorization") token: String): ResponseData<UserItem>

    @GET("v1/pengumuman")
    suspend fun getAnnouncement(@Header("Authorization") token: String): ResponseData<AnnouncementItem>


    @GET("v1/role")
    suspend fun getRole(@Header("Authorization") token: String): ResponseData<List<RoleItem>>

    @POST("v1/update-password")
    suspend fun updatePassword(
        @Header("Authorization") token: String,
        @Body request: UpdatePasswordRequest
    ): Response<ResponseData<UserItem>>

    @Multipart
    @POST("v1/update-profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part?,
        @Part("_method") method: RequestBody,
        @Part("nama_penanggung_jawab") personResponsible: RequestBody,
        @Part("nip_oprator") nip: RequestBody
    ): Response<ResponseData<UserItem>>

    @GET("v1/download-file/{id}")
    suspend fun insertLogDownload(
        @Header("Authorization") token: String,
        @Path("id") folderId: Int,
    ): Response<ResponseData<UploadItem>>


}