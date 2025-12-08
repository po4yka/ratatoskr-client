package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.remote.DownloadProgress
import com.po4yka.bitesizereader.domain.repository.SystemRepository
import com.po4yka.bitesizereader.util.FileSaver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform

class DownloadDatabaseUseCase(
    private val repository: SystemRepository,
    private val fileSaver: FileSaver
) {
    suspend operator fun invoke(fileName: String): Flow<DownloadProgress> {
        val tempPath = fileSaver.getInternalStoragePath(fileName)
        
        return repository.downloadDatabase(tempPath).transform { progress ->
            emit(progress)
            if (progress.isComplete) {
                // Download finished, move to public directory
                val finalPath = fileSaver.saveToDownloads(tempPath, fileName)
                if (finalPath == null) {
                    throw Exception("Failed to save file to Downloads directory")
                }
                // Optional: Emit a special "Saved" state or just rely on completion
            }
        }
    }
}
