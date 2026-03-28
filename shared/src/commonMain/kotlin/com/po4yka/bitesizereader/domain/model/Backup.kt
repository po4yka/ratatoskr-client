package com.po4yka.bitesizereader.domain.model

data class Backup(
    val id: Int,
    val type: String,
    val status: String,
    val fileSizeBytes: Long?,
    val itemsCount: Int?,
    val error: String?,
    val createdAt: String,
    val updatedAt: String,
)

data class BackupSchedule(
    val backupEnabled: Boolean,
    val backupFrequency: String,
    val backupRetentionCount: Int,
)

data class BackupRestoreResult(
    val imported: Int,
    val errors: Int,
    val total: Int,
)
