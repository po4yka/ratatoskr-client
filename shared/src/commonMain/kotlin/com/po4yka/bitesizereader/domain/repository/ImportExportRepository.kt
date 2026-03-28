package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.data.remote.dto.ImportJobDto

interface ImportExportRepository {
    suspend fun importBookmarks(
        fileBytes: ByteArray,
        fileName: String,
        summarize: Boolean = false,
        createTags: Boolean = true,
        targetCollectionId: Int? = null,
        skipDuplicates: Boolean = true,
    ): ImportJobDto

    suspend fun getImportJob(jobId: Int): ImportJobDto

    suspend fun listImportJobs(): List<ImportJobDto>

    suspend fun deleteImportJob(jobId: Int)

    suspend fun exportBookmarks(
        format: String = "json",
        tag: String? = null,
        collectionId: Int? = null,
    ): ByteArray
}
