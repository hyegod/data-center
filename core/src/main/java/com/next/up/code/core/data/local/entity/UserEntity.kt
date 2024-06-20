package com.next.up.code.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userTable")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
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