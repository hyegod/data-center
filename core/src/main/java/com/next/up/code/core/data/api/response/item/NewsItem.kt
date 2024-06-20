package com.next.up.code.core.data.api.response.item

import com.google.gson.annotations.SerializedName

data class NewsItem(
    val id: Int,
    @field:SerializedName("judul")
    val title: String,
    @field:SerializedName("deskripsi")
    val description: String,
    @field:SerializedName("gambar")
    val thumbnail: String
)
