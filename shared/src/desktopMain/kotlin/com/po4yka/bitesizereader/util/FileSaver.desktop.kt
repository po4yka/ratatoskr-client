package com.po4yka.bitesizereader.util

import java.io.File
import java.nio.file.Paths

/**
 * Desktop implementation of FileSaver
 */
actual class FileSaver {
    actual suspend fun saveToDownloads(
        sourcePath: String,
        fileName: String,
    ): String? {
        val sourceFile = File(sourcePath)
        if (!sourceFile.exists()) return null

        // On desktop, save to user's Downloads directory
        val userHome = System.getProperty("user.home")
        val downloadsDir = File(userHome, "Downloads")
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }

        val destFile = File(downloadsDir, fileName)
        return try {
            sourceFile.copyTo(destFile, overwrite = true)
            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    actual fun getInternalStoragePath(fileName: String): String {
        // Use system temp directory for temporary storage
        val tempDir = System.getProperty("java.io.tmpdir")
        return Paths.get(tempDir, "bitesizereader", fileName).toString().also {
            File(it).parentFile?.mkdirs()
        }
    }

    actual suspend fun importDatabase(
        sourcePath: String,
        targetDbName: String,
    ) {
        val sourceFile = File(sourcePath)
        if (!sourceFile.exists()) throw Exception("Source file not found")

        // Desktop database location (same as DatabaseDriverFactory location)
        val userHome = System.getProperty("user.home")
        val targetDir = File(userHome, ".bitesizereader")
        if (!targetDir.exists()) {
            targetDir.mkdirs()
        }

        val targetFile = File(targetDir, targetDbName)

        // Copy and overwrite
        sourceFile.copyTo(targetFile, overwrite = true)

        // Also try to close/delete WAL files if they exist
        File(targetFile.path + "-wal").delete()
        File(targetFile.path + "-shm").delete()
    }

    actual fun deleteIfExists(path: String) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }

    actual fun getFileSize(path: String): Long {
        val file = File(path)
        return if (file.exists()) file.length() else 0L
    }
}
