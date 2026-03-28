package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.remote.ImportExportApi
import com.po4yka.bitesizereader.data.remote.dto.ImportJobDto
import com.po4yka.bitesizereader.domain.repository.ImportExportRepository
import org.koin.core.annotation.Single

@Single(binds = [ImportExportRepository::class])
class ImportExportRepositoryImpl(
    private val api: ImportExportApi,
) : ImportExportRepository {
    override suspend fun importBookmarks(
        fileBytes: ByteArray,
        fileName: String,
        summarize: Boolean,
        createTags: Boolean,
        targetCollectionId: Int?,
        skipDuplicates: Boolean,
    ): ImportJobDto {
        val response = api.importBookmarks(
            fileBytes = fileBytes,
            fileName = fileName,
            summarize = summarize,
            createTags = createTags,
            targetCollectionId = targetCollectionId,
            skipDuplicates = skipDuplicates,
        )
        return requireNotNull(response.data) { "Server returned no data for import" }
    }

    override suspend fun getImportJob(jobId: Int): ImportJobDto {
        val response = api.getImportJob(jobId)
        return requireNotNull(response.data) { "Import job $jobId not found" }
    }

    override suspend fun listImportJobs(): List<ImportJobDto> {
        val response = api.listImportJobs()
        return response.data?.jobs ?: emptyList()
    }

    override suspend fun deleteImportJob(jobId: Int) {
        api.deleteImportJob(jobId)
    }

    override suspend fun exportBookmarks(
        format: String,
        tag: String?,
        collectionId: Int?,
    ): ByteArray = api.exportBookmarks(format = format, tag = tag, collectionId = collectionId)
}
