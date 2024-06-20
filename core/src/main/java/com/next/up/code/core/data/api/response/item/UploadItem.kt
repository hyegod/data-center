package com.next.up.code.core.data.api.response.item

import com.google.gson.annotations.SerializedName

data class UploadItem(
    val id: Int,
    @field: SerializedName("folder_name")
    val folderName: String
)