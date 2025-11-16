package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Generic API response wrapper used by all endpoints.
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ErrorDetail? = null,
    val meta: MetaInfo
)

@Serializable
data class ErrorDetail(
    val code: String,
    val message: String,
    val details: Map<String, String>? = null,
    @SerialName("correlation_id") val correlationId: String? = null
)

@Serializable
data class MetaInfo(
    val timestamp: String,
    val version: String? = null
)
