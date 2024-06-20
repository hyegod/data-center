package com.next.up.code.core.data.api.response.item

import com.google.gson.annotations.SerializedName

data class AnnouncementItem(
    val id: Int,
    @field:SerializedName("judul")
    val title: String,
    @field:SerializedName("tanggal")
    val date: String,
    val file: String?=null,
    val users: UserItem
)