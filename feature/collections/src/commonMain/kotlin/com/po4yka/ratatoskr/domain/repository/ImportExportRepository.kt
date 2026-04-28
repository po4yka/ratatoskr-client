package com.po4yka.ratatoskr.domain.repository

import com.po4yka.ratatoskr.domain.model.ImportJob

interface ImportExportRepository {
    suspend fun importBookmarks(
        fileBytes: ByteArray,
        fileName: String,
        summarize: Boolean = false,
        createTags: Boolean = true,
        targetCollectionId: Int? = null,
        skipDuplicates: Boolean = true,
    ): ImportJob

    suspend fun getImportJob(jobId: Int): ImportJob

    suspend fun listImportJobs(): List<ImportJob>

    suspend fun deleteImportJob(jobId: Int)

    suspend fun exportBookmarks(
        format: String = "json",
        tag: String? = null,
        collectionId: Int? = null,
    ): ByteArray
}
