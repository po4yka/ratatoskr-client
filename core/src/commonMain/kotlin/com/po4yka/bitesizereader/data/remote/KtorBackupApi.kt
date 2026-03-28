package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.BackupDeleteResponseDto
import com.po4yka.bitesizereader.data.remote.dto.BackupDto
import com.po4yka.bitesizereader.data.remote.dto.BackupListResponseDto
import com.po4yka.bitesizereader.data.remote.dto.BackupRestoreResponseDto
import com.po4yka.bitesizereader.data.remote.dto.BackupScheduleResponseDto
import com.po4yka.bitesizereader.data.remote.dto.UpdateBackupScheduleRequestDto
import com.po4yka.bitesizereader.util.retry.RetryPolicy
import com.po4yka.bitesizereader.util.retry.retryWithBackoff
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single(binds = [BackupApi::class])
class KtorBackupApi(private val client: HttpClient) : BackupApi {
    override suspend fun createBackup(): ApiResponseDto<BackupDto> {
        return client.post("v1/backups").body()
    }

    override suspend fun listBackups(): ApiResponseDto<BackupListResponseDto> =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            client.get("v1/backups").body()
        }

    override suspend fun getBackup(backupId: Int): ApiResponseDto<BackupDto> =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            client.get("v1/backups/$backupId").body()
        }

    override suspend fun downloadBackup(backupId: Int): ByteArray {
        return client.get("v1/backups/$backupId/download").bodyAsBytes()
    }

    override suspend fun deleteBackup(backupId: Int): ApiResponseDto<BackupDeleteResponseDto> {
        return client.delete("v1/backups/$backupId").body()
    }

    override suspend fun restoreBackup(fileBytes: ByteArray): ApiResponseDto<BackupRestoreResponseDto> {
        return client.submitFormWithBinaryData(
            url = "v1/backups/restore",
            formData = formData {
                append("file", fileBytes, Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=\"backup.zip\"")
                })
            },
        ).body()
    }

    override suspend fun getSchedule(): ApiResponseDto<BackupScheduleResponseDto> =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            client.get("v1/backups/schedule").body()
        }

    override suspend fun updateSchedule(
        request: UpdateBackupScheduleRequestDto,
    ): ApiResponseDto<BackupScheduleResponseDto> {
        return client.patch("v1/backups/schedule") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
