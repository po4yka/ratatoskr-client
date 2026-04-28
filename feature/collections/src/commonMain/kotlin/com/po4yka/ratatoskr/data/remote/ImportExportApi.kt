package com.po4yka.ratatoskr.data.remote

import com.po4yka.ratatoskr.data.remote.dto.ApiResponseDto
import com.po4yka.ratatoskr.data.remote.dto.ImportDeleteResponseDto
import com.po4yka.ratatoskr.data.remote.dto.ImportJobDto
import com.po4yka.ratatoskr.data.remote.dto.ImportJobListResponseDto

interface ImportExportApi {
    suspend fun importBookmarks(
        fileBytes: ByteArray,
        fileName: String,
        summarize: Boolean = false,
        createTags: Boolean = true,
        targetCollectionId: Int? = null,
        skipDuplicates: Boolean = true,
    ): ApiResponseDto<ImportJobDto>

    suspend fun getImportJob(jobId: Int): ApiResponseDto<ImportJobDto>

    suspend fun listImportJobs(): ApiResponseDto<ImportJobListResponseDto>

    suspend fun deleteImportJob(jobId: Int): ApiResponseDto<ImportDeleteResponseDto>

    suspend fun exportBookmarks(
        format: String = "json",
        tag: String? = null,
        collectionId: Int? = null,
    ): ByteArray
}
