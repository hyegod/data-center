package com.next.up.code.core.domain.model

data class Announcement(
    val id: Int,
    val title: String,
    val users: String,
    val date: String,
    val file: String? = null

)