package com.next.up.code.core.domain.model

data class BreadCrumbs(
    val id: Int? = 0,
    val fileName: String,
    val parentNameId: Int?
)