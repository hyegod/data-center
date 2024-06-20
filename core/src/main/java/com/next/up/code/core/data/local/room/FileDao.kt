package com.next.up.code.core.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.next.up.code.core.data.local.entity.FileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFile(file: List<FileEntity>)

    @Query("SELECT * FROM fileTable")
    fun getAllFile(): Flow<List<FileEntity>>

    @Query("SELECT * FROM fileTable WHERE fileName LIKE :searchQuery")
    fun searchFilesByName(searchQuery: String): Flow<List<FileEntity>>

    @RawQuery(observedEntities = [FileEntity::class])
    fun getAllFolder(query: SupportSQLiteQuery): Flow<List<FileEntity>>

    @Query("SELECT * FROM fileTable WHERE fileType = 'folder' AND parentNameId IS NULL ORDER BY fileName ASC")
    fun getFolders(): Flow<List<FileEntity>>
    @Query("SELECT * FROM fileTable WHERE fileType != 'folder' AND parentNameId IS NULL ORDER BY fileName ASC")
    fun getNonFolders(): Flow<List<FileEntity>>
    @Query("SELECT * FROM fileTable WHERE fileType = 'folder' AND parentNameId=:parentNameId ORDER BY fileName ASC")
    fun getDetailFolders(parentNameId: Int): Flow<List<FileEntity>>
    @Query("SELECT * FROM fileTable WHERE fileType != 'folder' AND parentNameId=:parentNameId ORDER BY fileName ASC")
    fun getDetailNonFolders(parentNameId: Int): Flow<List<FileEntity>>
    @Query(
        "SELECT * FROM fileTable WHERE fileType != 'folder' ORDER BY " +
                "CASE " +
                "WHEN timeAgo LIKE '%second%' THEN SUBSTR(timeAgo, 1, INSTR(timeAgo, ' second') - 1) + 0 " +
                "WHEN timeAgo LIKE '%minute%' THEN SUBSTR(timeAgo, 1, INSTR(timeAgo, ' minute') - 1) * 60 " +
                "WHEN timeAgo LIKE '%hour%' THEN SUBSTR(timeAgo, 1, INSTR(timeAgo, ' hour') - 1) * 3600 " +
                "WHEN timeAgo LIKE '%day%' THEN SUBSTR(timeAgo, 1, INSTR(timeAgo, ' day') - 1) * 86400 " +
                "WHEN timeAgo LIKE '%week%' THEN SUBSTR(timeAgo, 1, INSTR(timeAgo, ' week') - 1) * 604800 " +
                "ELSE 0 " +
                "END ASC LIMIT 3"
    )

    fun getLatestThreeFiles(): Flow<List<FileEntity>>
    @Query(
        "SELECT * FROM fileTable WHERE fileType != 'folder' ORDER BY " +
                "CASE " +
                "WHEN timeAgo LIKE '%second%' THEN SUBSTR(timeAgo, 1, INSTR(timeAgo, ' second') - 1) + 0 " +
                "WHEN timeAgo LIKE '%minute%' THEN SUBSTR(timeAgo, 1, INSTR(timeAgo, ' minute') - 1) * 60 " +
                "WHEN timeAgo LIKE '%hour%' THEN SUBSTR(timeAgo, 1, INSTR(timeAgo, ' hour') - 1) * 3600 " +
                "WHEN timeAgo LIKE '%day%' THEN SUBSTR(timeAgo, 1, INSTR(timeAgo, ' day') - 1) * 86400 " +
                "WHEN timeAgo LIKE '%week%' THEN SUBSTR(timeAgo, 1, INSTR(timeAgo, ' week') - 1) * 604800 " +
                "ELSE 0 " +
                "END"
    )
    fun getAllLatestFile(): Flow<List<FileEntity>>

    @Query("SELECT * FROM fileTable WHERE fileType IN ('docx', 'doc', 'pdf', 'doc', 'ppt', 'txt', 'pptx', 'ppt', 'xlsx', 'xls' ) ORDER BY fileName ASC")
    fun getFileDocument(): Flow<List<FileEntity>>

    @Query("SELECT * FROM fileTable WHERE fileType IN ('jpg', 'png', 'jpeg', 'gif', 'svg', 'webp') ORDER BY fileName ASC")
    fun getFileImage(): Flow<List<FileEntity>>

    @Query("SELECT * FROM fileTable WHERE fileType IN ('mp4', 'avi', 'mkv', 'wmv', 'mpg', 'mpeg', 'ogg', '3gp') ORDER BY fileName ASC")
    fun getFileVideo(): Flow<List<FileEntity>>

    @Query("SELECT * FROM fileTable WHERE fileType IN ('mp3', 'wav', 'opus') ORDER BY fileName ASC")
    fun getFileAudio(): Flow<List<FileEntity>>

    @Query("DELETE FROM fileTable")
    fun deleteAllFiles()

}