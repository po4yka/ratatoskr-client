package com.po4yka.bitesizereader.data.remote

import kotlinx.coroutines.flow.Flow

interface SystemApi {
    /**
     * Downloads the entire database backup.
     * Supports resuming via Range header logic (implemented in implementation).
     * @param outputFile Path to the local file to write to.
     * @return Flow of progress (bytes downloaded, total bytes).
     */
    suspend fun downloadDatabase(outputFile: String): Flow<DownloadProgress>
}

data class DownloadProgress(
    val bytesDownloaded: Long,
    val totalBytes: Long,
    val isComplete: Boolean = false
)
