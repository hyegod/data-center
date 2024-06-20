package com.next.up.code.core.data.local.room

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Database
import androidx.room.RoomDatabase
import com.next.up.code.core.data.local.entity.BreadCrumbsEntity
import com.next.up.code.core.data.local.entity.FileEntity
import com.next.up.code.core.data.local.entity.NewsEntity
import com.next.up.code.core.data.local.entity.UserEntity


@Database(
    entities = [FileEntity::class, UserEntity::class, NewsEntity::class, BreadCrumbsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DataCenterDatabase : RoomDatabase(), Parcelable {


    abstract fun fileDao(): FileDao
    abstract fun userDao(): UserDao
    abstract fun newsDao(): NewsDao
    abstract fun breadCrumbs(): BreadCrumbsDao


    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

}