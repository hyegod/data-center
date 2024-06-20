package com.next.up.code.core.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.next.up.code.core.data.local.entity.BreadCrumbsEntity
import com.next.up.code.core.domain.model.BreadCrumbs
import kotlinx.coroutines.flow.Flow

@Dao
interface BreadCrumbsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBreadCrumbs(breadCrumbsEntity: BreadCrumbsEntity)

    @Query("DELETE FROM breadTable WHERE id = (SELECT MAX(id) FROM breadTable)")
    fun deleteLastRecord()

    @Delete
    fun deleteBreadCrumbs(breadCrumbsEntity: BreadCrumbsEntity)

    @Query("SELECT * FROM breadTable")
    fun getAllBreadCrumbs(): Flow<List<BreadCrumbsEntity>>

}