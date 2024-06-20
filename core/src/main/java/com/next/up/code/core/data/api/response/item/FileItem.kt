package com.next.up.code.core.data.api.response.item

data class FileItem(
    val id: Int,
    val fileName: String,
    val fileType: String,
    val fileSize: String?,
    val timeAgo: String,
    val fileUrl: String?,
    val parentNameId: String?,
    val users: UserItem
)