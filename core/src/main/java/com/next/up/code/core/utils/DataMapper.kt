package com.next.up.code.core.utils

import com.next.up.code.core.data.api.response.item.FileItem
import com.next.up.code.core.data.api.response.item.LoginItem
import com.next.up.code.core.data.api.response.item.NewsItem
import com.next.up.code.core.data.api.response.item.RoleItem
import com.next.up.code.core.data.api.response.item.UserItem
import com.next.up.code.core.data.local.entity.BreadCrumbsEntity
import com.next.up.code.core.data.local.entity.FileEntity
import com.next.up.code.core.data.local.entity.NewsEntity
import com.next.up.code.core.data.local.entity.UserEntity
import com.next.up.code.core.domain.model.BreadCrumbs
import com.next.up.code.core.domain.model.LatestFile
import com.next.up.code.core.domain.model.Login
import com.next.up.code.core.domain.model.News
import com.next.up.code.core.domain.model.Roles
import com.next.up.code.core.domain.model.User

object DataMapper {
    fun loginItemToLogin(input: LoginItem): Login {
        return Login(
            input.token,
            input.id,
            input.name,
        )

    }

    fun breadCrumbsEntityToBreadCrumbs(input: BreadCrumbsEntity): BreadCrumbs {
        return BreadCrumbs(
            input.id,
            input.fileName,
            input.parentNameId,
        )

    }

    fun breadCrumbsToBreadCrumbsEntity(input: BreadCrumbs): BreadCrumbsEntity {
        return BreadCrumbsEntity(
            input.id,
            input.fileName,
            input.parentNameId,
        )

    }

    fun breadCrumbsEntityToBreadCrumbs(input: List<BreadCrumbsEntity>): List<BreadCrumbs> =
        input.map {
            BreadCrumbs(
                it.id,
                it.fileName,
                it.parentNameId
            )
        }




    fun fileItemToLatestFile(input: List<FileItem>): List<LatestFile> {

        return input.map { file ->
            val users = userItemToUser(file.users)
            LatestFile(
                file.id,
                file.fileName,
                file.fileType,
                file.fileSize,
                file.timeAgo,
                file.fileUrl,
                file.parentNameId,
                users.personResponsible.toString(),
                users.picture.toString(),
                users.roles.toString()
            )

        }

    }

    fun fileItemToFileEntity(input: List<FileItem>): List<FileEntity> {

        return input.map { file ->
            val users = userItemToUser(file.users)
            FileEntity(
                file.id,
                file.fileName,
                file.fileType,
                file.fileSize,
                file.timeAgo,
                file.fileUrl,
                file.parentNameId,
                users.personResponsible.toString(),
                users.picture.toString(),
                users.roles.toString()
            )

        }

    }

    fun roleItemToRole(input: List<RoleItem>): List<Roles> {

        return input.map { role ->
            Roles(
                role.id,
                role.name,
            )

        }

    }

    fun fileEntityToLatestFile(input: List<FileEntity>): List<LatestFile> {

        return input.map { file ->
            LatestFile(
                file.id,
                file.fileName,
                file.fileType,
                file.fileSize,
                file.timeAgo,
                file.fileUrl,
                file.parentNameId,
                file.user,
                file.picture,
                file.roles
            )

        }

    }

    private fun userItemToUser(input: UserItem): User {
        return User(
            input.id,
            input.name,
            input.email,
            input.picture,
            input.personResponsible,
            input.nip,
            input.roles,
            input.permission_edit,
            input.permission_delete,
            input.permission_upload,
            input.permission_create,
            input.permission_download,
        )

    }

    fun userEntityToUser(input: UserEntity?): User {

        return User(
            input?.id,
            input?.name,
            input?.email,
            input?.picture,
            input?.personResponsible,
            input?.nip,
            input?.roles,
            input?.permissionEdit,
            input?.permissionDelete,
            input?.permissionUpload,
            input?.permissionCreate,
            input?.permissionDownload,
        )

    }

    fun userItemToUserEntity(input: UserItem): UserEntity {
        return UserEntity(
            input.id,
            input.name,
            input.email,
            input.picture,
            input.personResponsible,
            input.nip,
            input.roles,
            input.permission_edit,
            input.permission_delete,
            input.permission_upload,
            input.permission_create,
            input.permission_download,
        )

    }

    fun newsEntityToNews(input: List<NewsEntity>): List<News> {

        return input.map { news ->
            News(
                news.id,
                news.title,
                news.description,
                news.thumbnail,
            )

        }

    }

    fun newsItemToNewsEntity(input: List<NewsItem>): List<NewsEntity> {

        return input.map { news ->
            NewsEntity(
                news.id,
                news.title,
                news.description,
                news.thumbnail,
            )

        }

    }


}