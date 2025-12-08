package com.po4yka.bitesizereader.util

expect class FileSaver {
    /**
     * Saves a file from a temporary location to a user-accessible location (Downloads/Documents).
     * @param sourcePath Absolute path to the source file.
     * @param fileName Desired name for the saved file.
     * @return The final path or URI as a string, or null if failed.
     */
    suspend fun saveToDownloads(sourcePath: String, fileName: String): String?
    
    /**
     * Gets a safe internal path for temporary storage before export.
     */
    fun getInternalStoragePath(fileName: String): String
}
