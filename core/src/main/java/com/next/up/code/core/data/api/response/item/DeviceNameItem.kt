package com.next.up.code.core.data.api.response.item

import com.google.gson.annotations.SerializedName

data class DeviceNameItem(
    @field:SerializedName("product_name") val deviceName: String,
)