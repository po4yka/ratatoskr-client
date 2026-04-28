package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.BackupRestoreResult
import com.po4yka.ratatoskr.domain.repository.BackupRepository
import org.koin.core.annotation.Factory

@Factory
class RestoreBackupUseCase(private val backupRepository: BackupRepository) {
    suspend operator fun invoke(fileBytes: ByteArray): BackupRestoreResult = backupRepository.restoreBackup(fileBytes)
}
