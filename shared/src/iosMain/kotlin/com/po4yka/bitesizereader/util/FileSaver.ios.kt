package com.po4yka.bitesizereader.util

import platform.Foundation.*

actual class FileSaver {
    actual suspend fun saveToDownloads(sourcePath: String, fileName: String): String? {
        val fileManager = NSFileManager.defaultManager
        
        // On iOS, "Downloads" isn't a standard accessible folder in the same way.
        // We typically save to Documents and ensure UIFileSharingEnabled is true,
        // or let the UI layer present a share sheet.
        // For this logic, we'll copy it to the Documents directory which is accessible via Files app
        // if Info.plist has LSSupportsOpeningDocumentsInPlace / UIFileSharingEnabled.
        
        val documentsUrl = fileManager.URLForDirectory(
            NSDocumentDirectory,
            NSUserDomainMask,
            null,
            true,
            null
        ) as? NSURL ?: return null
        
        val destUrl = documentsUrl.URLByAppendingPathComponent(fileName) ?: return null
        val sourceUrl = NSURL.fileURLWithPath(sourcePath)

        return try {
            if (fileManager.fileExistsAtPath(destUrl.path!!)) {
                fileManager.removeItemAtURL(destUrl, null)
            }
            fileManager.copyItemAtURL(sourceUrl, destUrl, null)
            destUrl.path
        } catch (e: Exception) {
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
}
