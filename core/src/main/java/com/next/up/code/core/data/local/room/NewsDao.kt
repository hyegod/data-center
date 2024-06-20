package com.next.up.code.core.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.next.up.code.core.data.local.entity.NewsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNews(news: List<NewsEntity>)

    @Query("SELECT * FROM news")
    fun getNews(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news limit 5")
    fun getNewsLatest(): Flow<List<NewsEntity>>

}