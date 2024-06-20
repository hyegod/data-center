package com.next.up.code.core.data.api.response

data class ResponseData<T>(
    val message : String,
    val success : Boolean,
    val data: T
)