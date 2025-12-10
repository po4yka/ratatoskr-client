package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.remote.DownloadProgress
import com.po4yka.bitesizereader.domain.repository.SystemRepository
import com.po4yka.bitesizereader.util.FileSaver
import com.po4yka.bitesizereader.util.config.AppConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import org.koin.core.annotation.Factory
import com.po4yka.bitesizereader.util.error.AppError

enum class DownloadMode {
    BACKUP,
    IMPORT,
}

private val logger = KotlinLogging.logger {}

@Factory
class DownloadDatabaseUseCase(
    private val repository: SystemRepository,
    private val fileSaver: FileSaver,
) {
    suspend operator fun invoke(
        fileName: String,
        mode: DownloadMode,
    ): Flow<DownloadProgress> {
        logger.info { "DownloadDatabaseUseCase invoked. Validating inputs. FileName: $fileName, Mode: $mode" }
        val tempPath = fileSaver.getInternalStoragePath(fileName)
        logger.debug { "Temporary storage path resolved: $tempPath" }

        return repository.downloadDatabase(tempPath).transform { progress ->
            if (!progress.isComplete) {
                emit(progress)
            } else {
                logger.info { "Download progress complete. Processing mode: $mode" }
                // Download finished, proceed based on mode before signaling completion
                when (mode) {
                    DownloadMode.BACKUP -> {
                        logger.info { "Executing BACKUP flow. Saving to Downloads." }
                        val finalPath = fileSaver.saveToDownloads(tempPath, fileName)
                        if (finalPath == null) {
                            val msg = "Failed to save file to Downloads directory"
                            logger.error { msg }
                            throw Exception(msg)
                        }
                        logger.info { "Backup saved successfully to: $finalPath" }
                    }
                    DownloadMode.IMPORT -> {
                        logger.info { "Executing IMPORT flow. Importing database from temp path." }
                        val size = fileSaver.getFileSize(tempPath)
                        if (size < 100) { // arbitrary small size for valid sqlite header (100 bytes is generous, header is 16 bytes but empty db is 4KB usually)
                             val msg = "Downloaded database file is too small ($size bytes). Aborting import."
                             logger.error { msg }
                             throw AppError.ValidationError("error.import.invalid", "Downloaded backup file is invalid or empty.")
                        }
                        fileSaver.importDatabase(tempPath, AppConfig.Database.NAME)
                        logger.info { "Database import executed successfully." }
                    }
                }
                // Emit completion only after post-processing succeeds
                emit(progress)
            }
        }
    }

    fun cleanupTemp(fileName: String) {
        val tempPath = fileSaver.getInternalStoragePath(fileName)
        logger.info { "Cleaning up temp file if exists: $tempPath" }
        fileSaver.deleteIfExists(tempPath)
    }
}
