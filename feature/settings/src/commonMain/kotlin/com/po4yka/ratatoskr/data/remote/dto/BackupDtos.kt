package com.po4yka.ratatoskr.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BackupDto(
    @SerialName("id") val id: Int,
    @SerialName("type") val type: String,
    @SerialName("status") val status: String,
    @SerialName("filePath") val filePath: String? = null,
    @SerialName("fileSizeBytes") val fileSizeBytes: Long? = null,
    @SerialName("itemsCount") val itemsCount: Int? = null,
    @SerialName("error") val error: String? = null,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String,
)

@Serializable
data class BackupListResponseDto(
    @SerialName("backups") val backups: List<BackupDto>,
)

@Serializable
data class BackupDeleteResponseDto(
    @SerialName("deleted") val deleted: Boolean,
    @SerialName("id") val id: Int,
)

@Serializable
data class BackupRestoreResponseDto(
    @SerialName("restored") val restored: Int,
    @SerialName("skipped") val skipped: Int,
    @SerialName("errors") val errors: List<String> = emptyList(),
)

@Serializable
data class BackupScheduleDto(
    @SerialName("backup_enabled") val backupEnabled: Boolean,
    @SerialName("backup_frequency") val backupFrequency: String,
    @SerialName("backup_retention_count") val backupRetentionCount: Int,
)

@Serializable
data class BackupScheduleResponseDto(
    @SerialName("schedule") val schedule: BackupScheduleDto,
)

@Serializable
data class UpdateBackupScheduleRequestDto(
    @SerialName("backup_enabled") val backupEnabled: Boolean? = null,
    @SerialName("backup_frequency") val backupFrequency: String? = null,
    @SerialName("backup_retention_count") val backupRetentionCount: Int? = null,
)
