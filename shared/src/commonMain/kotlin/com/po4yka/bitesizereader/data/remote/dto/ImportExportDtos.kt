package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImportJobDto(
    @SerialName("id") val id: Int,
    @SerialName("source_format") val sourceFormat: String,
    @SerialName("file_name") val fileName: String,
    @SerialName("status") val status: String,
    @SerialName("total_items") val totalItems: Int,
    @SerialName("processed_items") val processedItems: Int,
    @SerialName("created_items") val createdItems: Int,
    @SerialName("skipped_items") val skippedItems: Int,
    @SerialName("failed_items") val failedItems: Int,
    @SerialName("errors") val errors: List<String> = emptyList(),
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
)

@Serializable
data class ImportJobListResponseDto(
    @SerialName("jobs") val jobs: List<ImportJobDto>,
)

@Serializable
data class ImportDeleteResponseDto(
    @SerialName("deleted") val deleted: Boolean,
    @SerialName("id") val id: Int,
)
