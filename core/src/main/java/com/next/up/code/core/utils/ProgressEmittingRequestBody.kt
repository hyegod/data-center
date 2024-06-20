package com.next.up.code.core.utils

import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CompletableDeferred
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

class ProgressEmittingRequestBody(val file: File) : RequestBody() {

    private val progressSubject = PublishSubject.create<Int>()
    private val cancellationSignal = CompletableDeferred<Unit>()

    override fun contentType(): MediaType? {
        // Set the media type dynamically based on the file extension
        val mediaTypeString = getMediaType(file.extension)
        return mediaTypeString?.toMediaTypeOrNull()
    }

    override fun contentLength(): Long = file.length()

    override fun writeTo(sink: BufferedSink) {
        val buffer = ByteArray(BUFFER_SIZE)
        var uploaded: Long = 0
        val fileSize = file.length()

        FileInputStream(file).use { inputStream ->
            try {
                while (true) {
                    val read = inputStream.read(buffer)
                    if (read == -1) break

                    uploaded += read
                    sink.write(buffer, 0, read)

                    val progress = (((uploaded / fileSize.toDouble())) * 100).toInt()
                    progressSubject.onNext(progress)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                progressSubject.onError(e)
            }
        }
    }

    fun getProgressSubject() = progressSubject
    fun cancelUpload() {
        cancellationSignal.complete(Unit)
    }

    private fun getMediaType(extension: String): String? {
        // Map file extensions to media types as needed
        return when (extension.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "mp4" -> "video/mp4"
            // Add more mappings for other file types as needed
            else -> "application/octet-stream" // Default to binary data
        }
    }

    companion object {
        const val BUFFER_SIZE = 8192
    }
}
