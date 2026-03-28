package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Backup
import com.po4yka.bitesizereader.domain.repository.BackupRepository
import org.koin.core.annotation.Factory

@Factory
class CreateBackupUseCase(private val backupRepository: BackupRepository) {
    suspend operator fun invoke(): Backup = backupRepository.createBackup()
}
