package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.Backup
import com.po4yka.bitesizereader.domain.model.BackupRestoreResult
import com.po4yka.bitesizereader.domain.model.BackupSchedule

interface BackupRepository {
    suspend fun createBackup(): Backup

    suspend fun listBackups(): List<Backup>

    suspend fun getBackup(backupId: Int): Backup

    suspend fun downloadBackup(backupId: Int): ByteArray

    suspend fun deleteBackup(backupId: Int)

    suspend fun restoreBackup(fileBytes: ByteArray): BackupRestoreResult

    suspend fun getSchedule(): BackupSchedule

    suspend fun updateSchedule(
        backupEnabled: Boolean? = null,
        backupFrequency: String? = null,
        backupRetentionCount: Int? = null,
    ): BackupSchedule
}
