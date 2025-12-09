package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.remote.DownloadProgress
import com.po4yka.bitesizereader.domain.repository.SystemRepository
import com.po4yka.bitesizereader.util.FileSaver
import com.po4yka.bitesizereader.util.config.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform


enum class DownloadMode {
    BACKUP,
    IMPORT
}

class DownloadDatabaseUseCase(
    private val repository: SystemRepository,
    private val fileSaver: FileSaver
) {
    suspend operator fun invoke(fileName: String, mode: DownloadMode): Flow<DownloadProgress> {
        val tempPath = fileSaver.getInternalStoragePath(fileName)

        return repository.downloadDatabase(tempPath).transform { progress ->
            emit(progress)
            if (progress.isComplete) {
                // Download finished, proceed based on mode
                when (mode) {
                    DownloadMode.BACKUP -> {
                        val finalPath = fileSaver.saveToDownloads(tempPath, fileName)
                        if (finalPath == null) {
                            throw Exception("Failed to save file to Downloads directory")
                        }
                    }
                    DownloadMode.IMPORT -> {
                        fileSaver.importDatabase(tempPath, AppConfig.Database.NAME)
                    }
                }
            }
        }
    }
}
