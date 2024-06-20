package com.next.up.code.core.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.next.up.code.core.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity)

    @Query("SELECT * FROM userTable")
    fun getUser(): Flow<UserEntity?>

    @Query("SELECT * FROM userTable limit 1")
    fun setUser(): Flow<UserEntity?>

    @Query("DELETE FROM userTable")
    fun deleteAllUsers()


}