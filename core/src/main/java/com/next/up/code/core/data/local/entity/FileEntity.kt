package com.next.up.code.core.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fileTable",
    indices = [Index(value = ["fileType"]), Index(value = ["fileName"]), Index(value = ["timeAgo"])]
)
data class FileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val fileName: String,
    val fileType: String,
    val fileSize: String?,
    val timeAgo: String,
    val fileUrl: String?,
    val parentNameId: String?,
    val user: String,
    val picture: String,
    val roles: String,
)

