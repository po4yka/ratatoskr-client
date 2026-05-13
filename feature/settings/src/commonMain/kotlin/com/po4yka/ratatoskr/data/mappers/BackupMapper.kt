package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.api.generated.models.Backup as GeneratedBackup
import com.po4yka.ratatoskr.api.generated.models.BackupSchedule as GeneratedBackupSchedule
import com.po4yka.ratatoskr.data.remote.dto.BackupRestoreResponseDto
import com.po4yka.ratatoskr.domain.model.Backup
import com.po4yka.ratatoskr.domain.model.BackupRestoreResult
import com.po4yka.ratatoskr.domain.model.BackupSchedule

fun GeneratedBackup.toDomain(): Backup =
    Backup(
        id = id.toInt(),
        type = type,
        status = status,
        fileSizeBytes = fileSizeBytes,
        itemsCount = itemsCount?.toInt(),
        error = error,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString(),
    )

fun GeneratedBackupSchedule.toDomain(): BackupSchedule =
    BackupSchedule(
        backupEnabled = backupEnabled ?: false,
        backupFrequency = backupFrequency.orEmpty(),
        backupRetentionCount = backupRetentionCount?.toInt() ?: 0,
    )

fun BackupRestoreResponseDto.toDomain(): BackupRestoreResult =
    BackupRestoreResult(
        restored = restored,
        skipped = skipped,
        errors = errors,
    )
