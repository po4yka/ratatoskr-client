package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.data.remote.BackupApi
import com.po4yka.ratatoskr.data.remote.dto.UpdateBackupScheduleRequestDto
import com.po4yka.ratatoskr.domain.model.Backup
import com.po4yka.ratatoskr.domain.model.BackupRestoreResult
import com.po4yka.ratatoskr.domain.model.BackupSchedule
import com.po4yka.ratatoskr.domain.repository.BackupRepository
import org.koin.core.annotation.Single

@Single(binds = [BackupRepository::class])
class BackupRepositoryImpl(
    private val backupApi: BackupApi,
) : BackupRepository {
    override suspend fun createBackup(): Backup {
        val response = backupApi.createBackup()
        return requireNotNull(response.data) { "Server returned no data for backup creation" }.toDomain()
    }

    override suspend fun listBackups(): List<Backup> {
        val response = backupApi.listBackups()
        return response.data?.backups?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun getBackup(backupId: Int): Backup {
        val response = backupApi.getBackup(backupId)
        return requireNotNull(response.data) { "Backup $backupId not found" }.toDomain()
    }

    override suspend fun downloadBackup(backupId: Int): ByteArray = backupApi.downloadBackup(backupId)

    override suspend fun deleteBackup(backupId: Int) {
        backupApi.deleteBackup(backupId)
    }

    override suspend fun restoreBackup(fileBytes: ByteArray): BackupRestoreResult {
        val response = backupApi.restoreBackup(fileBytes)
        return requireNotNull(response.data) { "Server returned no data for backup restore" }.toDomain()
    }

    override suspend fun getSchedule(): BackupSchedule {
        val response = backupApi.getSchedule()
        return requireNotNull(response.data) { "Server returned no data for backup schedule" }.schedule.toDomain()
    }

    override suspend fun updateSchedule(
        backupEnabled: Boolean?,
        backupFrequency: String?,
        backupRetentionCount: Int?,
    ): BackupSchedule {
        val response =
            backupApi.updateSchedule(
                UpdateBackupScheduleRequestDto(
                    backupEnabled = backupEnabled,
                    backupFrequency = backupFrequency,
                    backupRetentionCount = backupRetentionCount,
                ),
            )
        return requireNotNull(response.data) { "Server returned no data for schedule update" }.schedule.toDomain()
    }
}
