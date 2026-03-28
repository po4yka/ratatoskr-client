package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.BackupRestoreResult
import com.po4yka.bitesizereader.domain.repository.BackupRepository
import org.koin.core.annotation.Factory

@Factory
class RestoreBackupUseCase(private val backupRepository: BackupRepository) {
    suspend operator fun invoke(fileBytes: ByteArray): BackupRestoreResult = backupRepository.restoreBackup(fileBytes)
}
