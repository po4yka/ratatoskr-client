@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.po4yka.bitesizereader.util

import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSLibraryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import io.github.oshai.kotlinlogging.KotlinLogging
import platform.Foundation.URLByAppendingPathComponent

private val logger = KotlinLogging.logger {}

actual class FileSaver {
    actual suspend fun saveToDownloads(
        sourcePath: String,
        fileName: String,
    ): String? {
        logger.info { "saveToDownloads called. Source: $sourcePath, FileName: $fileName" }
        val fileManager = NSFileManager.defaultManager

        // On iOS, "Downloads" isn't a standard accessible folder in the same way.
        // We typically save to Documents and ensure UIFileSharingEnabled is true,
        // or let the UI layer present a share sheet.
        // For this logic, we'll copy it to the Documents directory which is accessible via Files app
        // if Info.plist has LSSupportsOpeningDocumentsInPlace / UIFileSharingEnabled.

        val documentsUrl =
            fileManager.URLForDirectory(
                NSDocumentDirectory,
                NSUserDomainMask,
                null,
                true,
                null,
            ) as? NSURL ?: run {
                logger.error { "Could not access Documents directory" }
                return null
            }

        val destUrl = documentsUrl.URLByAppendingPathComponent(fileName) ?: return null
        val sourceUrl = NSURL.fileURLWithPath(sourcePath)

        return try {
            if (fileManager.fileExistsAtPath(destUrl.path!!)) {
                logger.info { "Destination file exists, removing: ${destUrl.path}" }
                fileManager.removeItemAtURL(destUrl, null)
            }
            fileManager.copyItemAtURL(sourceUrl, destUrl, null)
            logger.info { "File saved successfully to: ${destUrl.path}" }
            destUrl.path
        } catch (e: Exception) {
            logger.error(e) { "Error saving file to Documents" }
            e.printStackTrace()
            null
        }
    }

    actual fun getInternalStoragePath(fileName: String): String {
        val fileManager = NSFileManager.defaultManager
        val urls = fileManager.URLsForDirectory(NSCachesDirectory, NSUserDomainMask)
        val cacheDir = urls.first() as NSURL
        return cacheDir.URLByAppendingPathComponent(fileName)?.path ?: fileName
    }

    actual suspend fun importDatabase(
        sourcePath: String,
        targetDbName: String,
    ) {
        val fileManager = NSFileManager.defaultManager

        // iOS databases are typically stored in Library/Application Support
        // or directly in the Documents directory depending on SQLDelight configuration
        val libraryUrl =
            fileManager.URLForDirectory(
                NSLibraryDirectory,
                NSUserDomainMask,
                null,
                true,
                null,
            ) as? NSURL ?: throw Exception("Could not access Library directory")

        logger.info { "Importing database. Copying from $sourcePath to Library directory with name $targetDbName" }

        val destUrl =
            libraryUrl.URLByAppendingPathComponent(targetDbName)
                ?: throw Exception("Could not create destination URL")
        val sourceUrl = NSURL.fileURLWithPath(sourcePath)

        // Remove existing database if present
        if (fileManager.fileExistsAtPath(destUrl.path!!)) {
            fileManager.removeItemAtURL(destUrl, null)
        }

        // Copy the new database file
        // Copy the new database file
        val success = fileManager.copyItemAtURL(sourceUrl, destUrl, null)
        if (!success) {
            logger.error { "Failed to copy database file to ${destUrl.path}" }
            throw Exception("Failed to copy database file")
        }
        logger.info { "Database import successful." }
    }
}
