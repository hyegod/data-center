package com.next.up.code.core.data.local

import com.next.up.code.core.data.local.constants.SortType
import com.next.up.code.core.data.local.entity.BreadCrumbsEntity
import com.next.up.code.core.data.local.entity.FileEntity
import com.next.up.code.core.data.local.entity.NewsEntity
import com.next.up.code.core.data.local.entity.UserEntity
import com.next.up.code.core.data.local.room.BreadCrumbsDao
import com.next.up.code.core.data.local.room.FileDao
import com.next.up.code.core.data.local.room.NewsDao
import com.next.up.code.core.data.local.room.UserDao
import com.next.up.code.core.utils.SortUtil
import kotlinx.coroutines.flow.Flow

class LocalDataSource(
    private val fileDao: FileDao,
    private val userDao: UserDao,
    private val newsDao: NewsDao, private val breadCrumbsDao: BreadCrumbsDao
) {
    fun insertFile(fileList: List<FileEntity>) = fileDao.insertFile(fileList)
    fun getAllList(): Flow<List<FileEntity>> = fileDao.getAllFile()

    fun getAllFolder(
        sortType: SortType,
        parentNameId: Int?,
        roleId: Int? = null
    ): Flow<List<FileEntity>> {
        val query = SortUtil.getSortedFileQuery(sortType, parentNameId, roleId)
        return fileDao.getAllFolder(query)
    }

    fun searchFilesByName(query: String): Flow<List<FileEntity>> = fileDao.searchFilesByName(query)

    fun insertUser(user: UserEntity) = userDao.insertUser(user)
    fun deleteAllUsers() = userDao.deleteAllUsers()
    fun deleteAllFiles() = fileDao.deleteAllFiles()

    fun getUser(): Flow<UserEntity?> = userDao.getUser()
    fun setUser(): Flow<UserEntity?> = userDao.setUser()

    fun insertNews(news: List<NewsEntity>) = newsDao.insertNews(news)

    fun getNews(): Flow<List<NewsEntity>> = newsDao.getNews()
    fun getNewsLatest(): Flow<List<NewsEntity>> = newsDao.getNewsLatest()

    /* Bread Crumbs */
    fun insertBreadCrumbs(breadCrumbs: BreadCrumbsEntity) =
        breadCrumbsDao.insertBreadCrumbs(breadCrumbs)

    fun deleteLastRecord() = breadCrumbsDao.deleteLastRecord()
    fun deleteBreadCrumbs(breadCrumbs: BreadCrumbsEntity) =
        breadCrumbsDao.deleteBreadCrumbs(breadCrumbs)

    fun getAllBreadCrumbs(): Flow<List<BreadCrumbsEntity>> = breadCrumbsDao.getAllBreadCrumbs()
}