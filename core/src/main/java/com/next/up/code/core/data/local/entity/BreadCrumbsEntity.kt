package com.next.up.code.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "breadTable")
data class BreadCrumbsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = 0,
    val fileName: String,
    val parentNameId: Int?
)