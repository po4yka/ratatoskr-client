package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponseDto<T>(
    @SerialName("success") val success: Boolean,
    @SerialName("data") val data: T? = null,
    @SerialName("error") val error: ErrorResponseDto? = null,
    @SerialName("meta") val meta: MetaDto? = null,
)

@Serializable
data class MetaDto(
    @SerialName("correlation_id") val correlationId: String,
    @SerialName("timestamp") val timestamp: String,
    @SerialName("version") val version: String,
    @SerialName("build") val build: String? = null,
    // Pagination omitted for now as it's complex and not explicitly required for this task's scope (RequestsApi)
)
