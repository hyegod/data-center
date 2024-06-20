package com.next.up.code.core.utils

import androidx.sqlite.db.SimpleSQLiteQuery
import com.next.up.code.core.data.local.constants.SortType

object SortUtil {
    fun getSortedFileQuery(
        sortType: SortType,
        parentNameId: Int? = null,
        roleId: Int? = null
    ): SimpleSQLiteQuery {
        val simpleQuery = StringBuilder().append("SELECT * FROM fileTable ")
        when (sortType) {
            SortType.ASCENDING_FOLDER -> {
                simpleQuery.append(
                    "WHERE parentNameId IS NULL " +
                            "ORDER BY CASE WHEN fileType = 'folder' THEN 0 ELSE 1 END, fileName ASC"
                )
            }

            SortType.DESCENDING_FOLDER -> {
                simpleQuery.append(
                    "WHERE parentNameId IS NULL " +
                            "ORDER BY CASE WHEN fileType = 'folder' THEN 0 ELSE 1 END, fileName DESC"
                )

            }

            SortType.DETAIL_FOLDER -> {
                simpleQuery.append(
                    "WHERE parentNameId=:parentNameId " +
                            "ORDER BY CASE WHEN fileType = 'folder' THEN 0 ELSE 1 END, fileName ASC"
                )
            }

            SortType.SECTOR_FOLDER -> {
                simpleQuery.append("WHERE (fileType = 'folder' OR (fileType != 'folder' AND parentNameId IS NULL)) AND roles = 'bidang_upt' ORDER BY fileName ASC")
            }

            SortType.ROLES -> {
                simpleQuery.append("WHERE (fileType = 'folder' OR (fileType != 'folder' AND parentNameId IS NULL)) AND roles = :roleId ORDER BY fileName ASC")
            }

            SortType.DOCUMENT -> {
                simpleQuery.append("WHERE fileType IN ('docx', 'doc', 'pdf', 'doc', 'ppt', 'txt', 'pptx', 'ppt', 'xlsx', 'xls' ) ORDER BY fileName ASC")
            }

            SortType.IMAGE -> {
                simpleQuery.append("WHERE fileType IN ('jpg', 'png', 'jpeg', 'gif', 'svg', 'webp') ORDER BY fileName ASC")
            }

            SortType.VIDEO -> {
                simpleQuery.append("WHERE fileType IN ('mp4', 'avi', 'mkv', 'wmv', 'mpg', 'mpeg', 'ogg', '3gp') ORDER BY fileName ASC")
            }

            SortType.AUDIO -> {
                simpleQuery.append("WHERE fileType IN ('mp3', 'wav', 'opus') ORDER BY fileName ASC")
            }

            SortType.THREE_LATEST -> {
                simpleQuery.append(
                    "WHERE fileType != 'folder' ORDER BY " +
                            "CASE " +
                            "WHEN timeAgo LIKE '%second%' THEN SUBSTR(timeAgo, 1, INSTR(timeAgo, ' second') - 1) + 0 " +
                            "WHEN timeAgo LIKE '%minute%' THEN SUBSTR(timeAgo, 1, INSTR(timeAgo, ' minute') - 1) * 60 " +
                            "WHEN timeAgo LIKE '%hour%' THEN SUBSTR(timeAgo, 1, INSTR(timeAgo, ' hour') - 1) * 3600 " +
                            "WHEN timeAgo LIKE '%day%' THEN SUBSTR(timeAgo, 1, INSTR(timeAgo, ' day') - 1) * 86400 " +
                            "WHEN timeAgo LIKE '%week%' THEN SUBSTR(timeAgo, 1, INSTR(timeAgo, ' week') - 1) * 604800 " +
                            "ELSE 0 " +
                            "END ASC LIMIT 3"
                )
            }

            SortType.LATEST -> {
                simpleQuery.append(
                    "WHERE fileType != 'folder' ORDER BY " +
                            "CASE " +
                            "WHEN timeAgo LIKE '%second%' THEN SUBSTR(timeAgo, 1, INSTR(timeAgo, ' second') - 1) + 0 " +
                            "WHEN timeAgo LIKE '%minute%' THEN SUBSTR(timeAgo, 1, INSTR(timeAgo, ' minute') - 1) * 60 " +
                            "WHEN timeAgo LIKE '%hour%' THEN SUBSTR(timeAgo, 1, INSTR(timeAgo, ' hour') - 1) * 3600 " +
                            "WHEN timeAgo LIKE '%day%' THEN SUBSTR(timeAgo, 1, INSTR(timeAgo, ' day') - 1) * 86400 " +
                            "WHEN timeAgo LIKE '%week%' THEN SUBSTR(timeAgo, 1, INSTR(timeAgo, ' week') - 1) * 604800 " +
                            "ELSE 0 " +
                            "END"
                )
            }
        }

        val args = if (parentNameId != null) {
            if (roleId != null) {
                arrayOf(parentNameId, roleId)
            } else {
                arrayOf(parentNameId)
            }
        } else if (roleId != null) {
            arrayOf(roleId)
        } else {
            emptyArray()
        }

        return SimpleSQLiteQuery(simpleQuery.toString(), args)
    }
}