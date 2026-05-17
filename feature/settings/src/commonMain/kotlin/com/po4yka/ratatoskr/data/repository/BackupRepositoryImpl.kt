package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.api.generated.api.BackupsApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.V1BackupsScheduleRequest
import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.data.remote.dto.BackupRestoreResponseDto
import com.po4yka.ratatoskr.domain.model.Backup
import com.po4yka.ratatoskr.domain.model.BackupRestoreResult
import com.po4yka.ratatoskr.domain.model.BackupSchedule
import com.po4yka.ratatoskr.domain.repository.BackupRepository
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import org.koin.core.annotation.Single

private val parserJson = Json { ignoreUnknownKeys = true }

@Single(binds = [BackupRepository::class])
class BackupRepositoryImpl : BackupRepository {
    override suspend fun createBackup(): Backup {
        val envelope = BackupsApi.createBackupV1BackupsPost().unwrap()
        return requireNotNull(envelope.`data`) { "Server returned no data for backup creation" }.toDomain()
    }

    override suspend fun listBackups(): List<Backup> {
        val envelope = BackupsApi.listBackupsV1BackupsGet().unwrap()
        return envelope.`data`?.backups?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun getBackup(backupId: Int): Backup {
        val envelope = BackupsApi.getBackupV1BackupsBackupIdGet(backupId = backupId.toLong()).unwrap()
        return requireNotNull(envelope.`data`) { "Backup $backupId not found" }.toDomain()
    }

    override suspend fun downloadBackup(backupId: Int): ByteArray {
        val response =
            BackupsApi.downloadBackupV1BackupsBackupIdDownloadGet(
                backupId = backupId.toLong(),
            ).unwrap()
        return response.bodyAsBytes()
    }

    override suspend fun deleteBackup(backupId: Int) {
        BackupsApi.deleteBackupV1BackupsBackupIdDelete(backupId = backupId.toLong()).unwrap()
    }

    override suspend fun restoreBackup(fileBytes: ByteArray): BackupRestoreResult {
        val multipart =
            MultiPartFormDataContent(
                formData {
                    append(
                        "file",
                        fileBytes,
                        Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"backup.zip\"")
                        },
                    )
                },
            )
        val envelope = BackupsApi.restoreBackupV1BackupsRestorePost(body = multipart).unwrap()
        val dataElement = requireNotNull(envelope.`data`) { "Server returned no data for backup restore" }
        return parserJson.decodeFromJsonElement<BackupRestoreResponseDto>(dataElement).toDomain()
    }

    override suspend fun getSchedule(): BackupSchedule {
        val envelope = BackupsApi.getBackupScheduleV1BackupsScheduleGet().unwrap()
        return requireNotNull(envelope.`data`) { "Server returned no data for backup schedule" }.schedule.toDomain()
    }

    override suspend fun updateSchedule(
        backupEnabled: Boolean?,
        backupFrequency: String?,
        backupRetentionCount: Int?,
    ): BackupSchedule {
        val envelope =
            BackupsApi.updateBackupScheduleV1BackupsSchedulePatch(
                body =
                    V1BackupsScheduleRequest(
                        backupEnabled = backupEnabled,
                        backupFrequency = backupFrequency,
                        backupRetentionCount = backupRetentionCount?.toLong(),
                    ),
            ).unwrap()
        return requireNotNull(envelope.`data`) { "Server returned no data for schedule update" }.schedule.toDomain()
    }
}
