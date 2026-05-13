package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.api.generated.api.BackupsApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.V1BackupsScheduleRequest
import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.data.remote.dto.ApiResponseDto
import com.po4yka.ratatoskr.data.remote.dto.BackupDeleteResponseDto
import com.po4yka.ratatoskr.data.remote.dto.BackupDto
import com.po4yka.ratatoskr.data.remote.dto.BackupListResponseDto
import com.po4yka.ratatoskr.data.remote.dto.BackupRestoreResponseDto
import com.po4yka.ratatoskr.data.remote.dto.BackupScheduleResponseDto
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
        val element = BackupsApi.createBackupV1BackupsPost().unwrap()
        val envelope = parserJson.decodeFromJsonElement<ApiResponseDto<BackupDto>>(element)
        return requireNotNull(envelope.data) { "Server returned no data for backup creation" }.toDomain()
    }

    override suspend fun listBackups(): List<Backup> {
        val element = BackupsApi.listBackupsV1BackupsGet().unwrap()
        val envelope = parserJson.decodeFromJsonElement<ApiResponseDto<BackupListResponseDto>>(element)
        return envelope.data?.backups?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun getBackup(backupId: Int): Backup {
        val element = BackupsApi.getBackupV1BackupsBackupIdGet(backupId = backupId.toLong()).unwrap()
        val envelope = parserJson.decodeFromJsonElement<ApiResponseDto<BackupDto>>(element)
        return requireNotNull(envelope.data) { "Backup $backupId not found" }.toDomain()
    }

    override suspend fun downloadBackup(backupId: Int): ByteArray {
        val response = BackupsApi.downloadBackupV1BackupsBackupIdDownloadGet(
            backupId = backupId.toLong(),
        ).unwrap()
        return response.bodyAsBytes()
    }

    override suspend fun deleteBackup(backupId: Int) {
        val element = BackupsApi.deleteBackupV1BackupsBackupIdDelete(backupId = backupId.toLong()).unwrap()
        parserJson.decodeFromJsonElement<ApiResponseDto<BackupDeleteResponseDto>>(element)
    }

    override suspend fun restoreBackup(fileBytes: ByteArray): BackupRestoreResult {
        val multipart = MultiPartFormDataContent(
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
        val element = BackupsApi.restoreBackupV1BackupsRestorePost(body = multipart).unwrap()
        val envelope = parserJson.decodeFromJsonElement<ApiResponseDto<BackupRestoreResponseDto>>(element)
        return requireNotNull(envelope.data) { "Server returned no data for backup restore" }.toDomain()
    }

    override suspend fun getSchedule(): BackupSchedule {
        val element = BackupsApi.getBackupScheduleV1BackupsScheduleGet().unwrap()
        val envelope = parserJson.decodeFromJsonElement<ApiResponseDto<BackupScheduleResponseDto>>(element)
        return requireNotNull(envelope.data) { "Server returned no data for backup schedule" }.schedule.toDomain()
    }

    override suspend fun updateSchedule(
        backupEnabled: Boolean?,
        backupFrequency: String?,
        backupRetentionCount: Int?,
    ): BackupSchedule {
        val element = BackupsApi.updateBackupScheduleV1BackupsSchedulePatch(
            body = V1BackupsScheduleRequest(
                backupEnabled = backupEnabled,
                backupFrequency = backupFrequency,
                backupRetentionCount = backupRetentionCount?.toLong(),
            ),
        ).unwrap()
        val envelope = parserJson.decodeFromJsonElement<ApiResponseDto<BackupScheduleResponseDto>>(element)
        return requireNotNull(envelope.data) { "Server returned no data for schedule update" }.schedule.toDomain()
    }
}
