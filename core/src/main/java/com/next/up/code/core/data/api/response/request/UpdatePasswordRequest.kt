package com.next.up.code.core.data.api.response.request

data class UpdatePasswordRequest(
    val current_password: String,
    val new_password: String,
    val new_password_confirmation: String
)
