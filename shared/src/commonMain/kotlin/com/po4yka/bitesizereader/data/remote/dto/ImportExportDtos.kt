package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImportJobDto(
    @SerialName("id") val id: Int,
    @SerialName("sourceFormat") val sourceFormat: String,
    @SerialName("fileName") val fileName: String? = null,
    @SerialName("status") val status: String,
    @SerialName("totalItems") val totalItems: Int,
    @SerialName("processedItems") val processedItems: Int,
    @SerialName("createdItems") val createdItems: Int,
    @SerialName("skippedItems") val skippedItems: Int,
    @SerialName("failedItems") val failedItems: Int,
    @SerialName("errors") val errors: List<String> = emptyList(),
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String,
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
