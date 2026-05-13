package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.api.generated.Api
import com.po4yka.ratatoskr.api.generated.api.ImportExportApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.ExportBookmarksV1ExportGetFormat
import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.data.remote.dto.ImportJobDto
import com.po4yka.ratatoskr.domain.model.ImportJob
import com.po4yka.ratatoskr.domain.repository.ImportExportRepository
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.JsonElement
import org.koin.core.annotation.Single

@Single(binds = [ImportExportRepository::class])
class ImportExportRepositoryImpl : ImportExportRepository {
    override suspend fun importBookmarks(
        fileBytes: ByteArray,
        fileName: String,
        summarize: Boolean,
        createTags: Boolean,
        targetCollectionId: Int?,
        skipDuplicates: Boolean,
    ): ImportJob {
        val optionsJson =
            buildString {
                append("{")
                append("\"summarize\":$summarize,")
                append("\"create_tags\":$createTags,")
                append("\"skip_duplicates\":$skipDuplicates")
                if (targetCollectionId != null) {
                    append(",\"target_collection_id\":$targetCollectionId")
                }
                append("}")
            }
        val multipart =
            MultiPartFormDataContent(
                formData {
                    append(
                        "file",
                        fileBytes,
                        Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                        },
                    )
                    append("options", optionsJson)
                },
            )
        val data: JsonElement? =
            ImportExportApi.importBookmarksV1ImportPost(body = multipart).unwrap().data
        val dto = decodeImportJobOrNull(data)
        return requireNotNull(dto) { "Server returned no data for import" }.toDomain()
    }

    override suspend fun getImportJob(jobId: Int): ImportJob {
        val data: JsonElement? =
            ImportExportApi.getImportJobV1ImportJobIdGet(jobId = jobId.toLong()).unwrap().data
        val dto = decodeImportJobOrNull(data)
        return requireNotNull(dto) { "Import job $jobId not found" }.toDomain()
    }

    override suspend fun listImportJobs(): List<ImportJob> {
        val data = ImportExportApi.listImportJobsV1ImportGet().unwrap().data
        return data?.jobs.orEmpty().mapNotNull { decodeImportJobOrNull(it)?.toDomain() }
    }

    override suspend fun deleteImportJob(jobId: Int) {
        ImportExportApi.deleteImportJobV1ImportJobIdDelete(jobId = jobId.toLong()).unwrap()
    }

    override suspend fun exportBookmarks(
        format: String,
        tag: String?,
        collectionId: Int?,
    ): ByteArray {
        val raw =
            ImportExportApi.exportBookmarksV1ExportGet(
                format = format.toExportFormat(),
                tag = tag,
                collectionId = collectionId?.toLong(),
            ).unwrap()
        return raw.encodeToByteArray()
    }
}

private fun String.toExportFormat(): ExportBookmarksV1ExportGetFormat? =
    when (this.lowercase()) {
        "json" -> ExportBookmarksV1ExportGetFormat.JSON
        "csv" -> ExportBookmarksV1ExportGetFormat.CSV
        "html" -> ExportBookmarksV1ExportGetFormat.HTML
        else -> null
    }

/**
 * The generated `ImportJob` model is a `typealias` for `JsonElement` because
 * the upstream spec lacks a concrete schema. Decode the JSON payload into the
 * hand-written [ImportJobDto] that mirrors the actual wire format.
 */
private fun decodeImportJobOrNull(element: JsonElement?): ImportJobDto? {
    if (element == null || element is kotlinx.serialization.json.JsonNull) return null
    return Api.json.decodeFromJsonElement(ImportJobDto.serializer(), element)
}
