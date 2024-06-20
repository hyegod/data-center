package com.next.up.code.core.domain.model

data class User(
    val id: Int?,
    val name: String?,
    val email: String?,
    val picture: String?,
    val personResponsible: String?,
    val nip: String?,
    val roles: String?,
    val permissionEdit: String?,
    val permissionDelete: String?,
    val permissionUpload: String?,
    val permissionCreate: String?,
    val permissionDownload: String?,
)