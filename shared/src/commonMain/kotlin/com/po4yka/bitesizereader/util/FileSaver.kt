package com.po4yka.bitesizereader.util

expect class FileSaver {
    /**
     * Saves a file from a temporary location to a user-accessible location (Downloads/Documents).
     * @param sourcePath Absolute path to the source file.
     * @param fileName Desired name for the saved file.
     * @return The final path or URI as a string, or null if failed.
     */
    suspend fun saveToDownloads(
        sourcePath: String,
        fileName: String,
    ): String?

    /**
     * Gets a safe internal path for temporary storage before export.
     */
    fun getInternalStoragePath(fileName: String): String

    /**
     * Imports a database file from a source path to the application's internal database directory.
     * @param sourcePath Absolute path to the source database file (e.g. from temp storage).
     * @param targetDbName The name of the database to overwrite (e.g. "bite_size_reader.db").
     */
    suspend fun importDatabase(
        sourcePath: String,
        targetDbName: String,
    )

    /**
     * Deletes a file at the given path if it exists.
     */
    fun deleteIfExists(path: String)

    /**
     * Gets the size of the file at the given path in bytes.
     * Returns 0 if the file does not exist.
     */
    fun getFileSize(path: String): Long
}
