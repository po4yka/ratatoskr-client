package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.api.generated.Api
import com.po4yka.ratatoskr.api.generated.api.ImportExportApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.ExportBookmarksV1ExportGetFormat
import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.data.remote.dto.ImportJobDto
import com.po4yka.ratatoskr.data.remote.dto.ImportJobListResponseDto
import com.po4yka.ratatoskr.domain.model.ImportJob
import com.po4yka.ratatoskr.domain.repository.ImportExportRepository
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
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
        val response =
            ImportExportApi.importBookmarksV1ImportPost(body = multipart)
                .unwrap()
                .decodeEnvelope<ImportJobDto>()
        return requireNotNull(response) { "Server returned no data for import" }.toDomain()
    }

    override suspend fun getImportJob(jobId: Int): ImportJob {
        val response =
            ImportExportApi.getImportJobV1ImportJobIdGet(jobId = jobId.toLong())
                .unwrap()
                .decodeEnvelope<ImportJobDto>()
        return requireNotNull(response) { "Import job $jobId not found" }.toDomain()
    }

    override suspend fun listImportJobs(): List<ImportJob> {
        val envelope =
            ImportExportApi.listImportJobsV1ImportGet()
                .unwrap()
                .decodeEnvelope<ImportJobListResponseDto>()
        return envelope?.jobs?.map { it.toDomain() } ?: emptyList()
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

private inline fun <reified T> JsonElement.decodeEnvelope(): T? {
    val obj = (this as? JsonObject) ?: return null
    val data = obj["data"] ?: return null
    if (data is kotlinx.serialization.json.JsonNull) return null
    return Api.json.decodeFromJsonElement(
        kotlinx.serialization.serializer<T>(),
        data,
    )
}
