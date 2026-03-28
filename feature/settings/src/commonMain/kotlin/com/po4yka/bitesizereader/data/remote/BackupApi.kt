package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.BackupDeleteResponseDto
import com.po4yka.bitesizereader.data.remote.dto.BackupDto
import com.po4yka.bitesizereader.data.remote.dto.BackupListResponseDto
import com.po4yka.bitesizereader.data.remote.dto.BackupRestoreResponseDto
import com.po4yka.bitesizereader.data.remote.dto.BackupScheduleResponseDto
import com.po4yka.bitesizereader.data.remote.dto.UpdateBackupScheduleRequestDto

interface BackupApi {
    suspend fun createBackup(): ApiResponseDto<BackupDto>

    suspend fun listBackups(): ApiResponseDto<BackupListResponseDto>

    suspend fun getBackup(backupId: Int): ApiResponseDto<BackupDto>

    suspend fun downloadBackup(backupId: Int): ByteArray

    suspend fun deleteBackup(backupId: Int): ApiResponseDto<BackupDeleteResponseDto>

    suspend fun restoreBackup(fileBytes: ByteArray): ApiResponseDto<BackupRestoreResponseDto>

    suspend fun getSchedule(): ApiResponseDto<BackupScheduleResponseDto>

    suspend fun updateSchedule(request: UpdateBackupScheduleRequestDto): ApiResponseDto<BackupScheduleResponseDto>
}
