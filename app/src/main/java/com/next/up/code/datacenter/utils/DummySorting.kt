package com.next.up.code.datacenter.utils

import com.next.up.code.core.domain.model.Roles

data class DummySorting(
    val id: Int,
    val sortingName: String
)

val sorting = listOf(
    DummySorting(
        1,
        "A-Z"
    ),
    DummySorting(
        2,
        "Z-A"
    )
)

val dummyRoles = listOf(
    Roles(
        1,
        "",

        ),
    Roles(
        2,
        "Bidang Jalan",

        ),
    Roles(
        3,
        "Bidang Kontruksi",

        ),
    Roles(
        4,
        "Bidang Sekop",

        ),
    Roles(
        5,
        "Bidang Listrik",

        ),
)
