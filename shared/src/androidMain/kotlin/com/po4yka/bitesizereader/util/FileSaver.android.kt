package com.po4yka.bitesizereader.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileInputStream

actual class FileSaver(private val context: Context) {
    actual suspend fun saveToDownloads(
        sourcePath: String,
        fileName: String,
    ): String? {
        val sourceFile = File(sourcePath)
        if (!sourceFile.exists()) return null

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues =
                ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/x-sqlite3")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { output ->
                    FileInputStream(sourceFile).use { input ->
                        input.copyTo(output)
                    }
                }
                it.toString()
            }
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val destFile = File(downloadsDir, fileName)
            sourceFile.copyTo(destFile, overwrite = true)
            destFile.absolutePath
        }
    }

    actual fun getInternalStoragePath(fileName: String): String {
        return File(context.filesDir, fileName).absolutePath
    }

    actual suspend fun importDatabase(
        sourcePath: String,
        targetDbName: String,
    ) {
        val sourceFile = File(sourcePath)
        if (!sourceFile.exists()) throw Exception("Source file not found")

        val targetFile = context.getDatabasePath(targetDbName)

        // Ensure parent directory exists
        targetFile.parentFile?.mkdirs()

        // Copy and overwrite
        sourceFile.copyTo(targetFile, overwrite = true)

        // Also try to close/delete WAL files if they exist to prevent corruption/inconsistency
        File(targetFile.path + "-wal").delete()
        File(targetFile.path + "-shm").delete()
    }
}
