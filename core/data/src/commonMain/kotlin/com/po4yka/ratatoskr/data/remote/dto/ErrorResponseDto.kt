package com.po4yka.ratatoskr.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Structured error details matching OpenAPI ErrorObject schema.
 * Provides machine-readable error codes for programmatic handling.
 */
@Serializable
data class ErrorResponseDto(
    /** Machine-readable error code (e.g., "VALIDATION_ERROR", "TOKEN_EXPIRED") */
    @SerialName("code") val code: String,
    /** Error category for client-side handling logic */
    @SerialName("errorType") val errorType: String? = null,
    /** Human-readable error message */
    @SerialName("message") val message: String,
    /** Whether the client should retry the request */
    @SerialName("retryable") val retryable: Boolean = false,
    /** Additional error context (field errors, resource IDs, etc.) */
    @SerialName("details") val details: JsonObject? = null,
    /** Request correlation ID for debugging */
    @SerialName("correlation_id") val correlationId: String? = null,
    /** Seconds to wait before retrying (for rate limit errors) */
    @SerialName("retry_after") val retryAfter: Int? = null,
)
