package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.data.remote.dto.BackupDto
import com.po4yka.ratatoskr.data.remote.dto.BackupRestoreResponseDto
import com.po4yka.ratatoskr.data.remote.dto.BackupScheduleDto
import com.po4yka.ratatoskr.domain.model.Backup
import com.po4yka.ratatoskr.domain.model.BackupRestoreResult
import com.po4yka.ratatoskr.domain.model.BackupSchedule

fun BackupDto.toDomain(): Backup =
    Backup(
        id = id,
        type = type,
        status = status,
        fileSizeBytes = fileSizeBytes,
        itemsCount = itemsCount,
        error = error,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun BackupScheduleDto.toDomain(): BackupSchedule =
    BackupSchedule(
        backupEnabled = backupEnabled,
        backupFrequency = backupFrequency,
        backupRetentionCount = backupRetentionCount,
    )

fun BackupRestoreResponseDto.toDomain(): BackupRestoreResult =
    BackupRestoreResult(
        restored = restored,
        skipped = skipped,
        errors = errors,
    )
