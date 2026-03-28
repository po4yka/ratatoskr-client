package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.ImportDeleteResponseDto
import com.po4yka.bitesizereader.data.remote.dto.ImportJobDto
import com.po4yka.bitesizereader.data.remote.dto.ImportJobListResponseDto
import com.po4yka.bitesizereader.util.retry.RetryPolicy
import com.po4yka.bitesizereader.util.retry.retryWithBackoff
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import org.koin.core.annotation.Single

@Single(binds = [ImportExportApi::class])
class KtorImportExportApi(private val client: HttpClient) : ImportExportApi {
    override suspend fun importBookmarks(
        fileBytes: ByteArray,
        fileName: String,
        summarize: Boolean,
        createTags: Boolean,
        targetCollectionId: Int?,
        skipDuplicates: Boolean,
    ): ApiResponseDto<ImportJobDto> {
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
        return client.submitFormWithBinaryData(
            url = "v1/import",
            formData =
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
        ).body()
    }

    override suspend fun getImportJob(jobId: Int): ApiResponseDto<ImportJobDto> =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            client.get("v1/import/$jobId").body()
        }

    override suspend fun listImportJobs(): ApiResponseDto<ImportJobListResponseDto> =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            client.get("v1/import").body()
        }

    override suspend fun deleteImportJob(jobId: Int): ApiResponseDto<ImportDeleteResponseDto> {
        return client.delete("v1/import/$jobId").body()
    }

    override suspend fun exportBookmarks(
        format: String,
        tag: String?,
        collectionId: Int?,
    ): ByteArray {
        return client.get("v1/export") {
            parameter("format", format)
            tag?.let { parameter("tag", it) }
            collectionId?.let { parameter("collection_id", it) }
        }.bodyAsBytes()
    }
}
