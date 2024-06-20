package com.next.up.code.core.data.api.response.item

import com.google.gson.annotations.SerializedName

data class UserItem(
    val id: Int?,
    val name: String?,
    val email: String?,
    val picture: String?,
    @field:SerializedName("nama_penanggung_jawab")
    val personResponsible: String?,
    @field:SerializedName("nip_oprator")
    val nip: String?,
    @field:SerializedName("roles_bidang_id")
    val roles: String?,
    val permission_edit: String?,
    val permission_delete: String?,
    val permission_upload: String?,
    val permission_create: String?,
    val permission_download: String?,
)