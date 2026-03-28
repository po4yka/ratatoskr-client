package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.remote.ImportExportApi
import com.po4yka.bitesizereader.domain.model.ImportJob
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
    ): ImportJob {
        val response = api.importBookmarks(
            fileBytes = fileBytes,
            fileName = fileName,
            summarize = summarize,
            createTags = createTags,
            targetCollectionId = targetCollectionId,
            skipDuplicates = skipDuplicates,
        )
        return requireNotNull(response.data) { "Server returned no data for import" }.toDomain()
    }

    override suspend fun getImportJob(jobId: Int): ImportJob {
        val response = api.getImportJob(jobId)
        return requireNotNull(response.data) { "Import job $jobId not found" }.toDomain()
    }

    override suspend fun listImportJobs(): List<ImportJob> {
        val response = api.listImportJobs()
        return response.data?.jobs?.map { it.toDomain() } ?: emptyList()
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
