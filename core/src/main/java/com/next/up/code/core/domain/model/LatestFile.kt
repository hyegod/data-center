package com.next.up.code.core.domain.model

data class LatestFile(
    val id: Int,
    val fileName: String,
    val fileType: String,
    val fileSize: String?,
    val timeAgo: String,
    val fileUrl: String?,
    val parentNameId: String?,
    val user: String,
    val picture: String,
    val roles: String
)