package com.next.up.code.core.utils

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.net.URL

object Utils {
    fun readExcelFileFromUrl(url: String) {
        try {
            val connection = URL(url).openConnection()
            val excelStream = connection.getInputStream()
            val workbook = WorkbookFactory.create(excelStream)

            // Baca spreadsheet pertama (indeks 0)
            val sheet = workbook.getSheetAt(0)

            // Iterasi melalui baris dan sel
            for (row in sheet) {
                for (cell in row) {
                    when (cell.cellTypeEnum) {
                        CellType.STRING -> println(cell.stringCellValue)
                        CellType.NUMERIC -> println(cell.numericCellValue)
                        CellType.BOOLEAN -> println(cell.booleanCellValue)
                        else -> println("Tipe sel tidak dikenali")
                    }
                }
            }

            // Tutup aliran Excel
            excelStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setupRecycleView(
        adapter: RecyclerView.Adapter<*>, recycleView: RecyclerView?, context: Context
    ) {
        with(recycleView) {
            this?.adapter = adapter
            this?.layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.VERTICAL, false
            )
            this?.setHasFixedSize(true)
        }
    }

    fun getFileExtension(fileName: String): String {
        val lastDotIndex = fileName.lastIndexOf('.')
        return if (lastDotIndex == -1) {
            "" // No file extension found
        } else {
            fileName.substring(lastDotIndex + 1).lowercase()
        }
    }


}