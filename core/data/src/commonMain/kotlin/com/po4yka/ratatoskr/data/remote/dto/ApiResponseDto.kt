package com.po4yka.ratatoskr.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Standard API response envelope matching OpenAPI BaseSuccessResponse/ErrorResponse.
 */
@Serializable
data class ApiResponseDto<T>(
    @SerialName("success") val success: Boolean,
    @SerialName("data") val data: T? = null,
    @SerialName("error") val error: ErrorResponseDto? = null,
    @SerialName("meta") val meta: MetaDto? = null,
)

/**
 * Response metadata for observability and pagination.
 */
@Serializable
data class MetaDto(
    /** Request correlation ID for debugging (always present, may be empty string) */
    @SerialName("correlation_id") val correlationId: String,
    /** ISO 8601 response timestamp */
    @SerialName("timestamp") val timestamp: String,
    /** API version string */
    @SerialName("version") val version: String,
    /** Build identifier (if available) */
    @SerialName("build") val build: String? = null,
    /** Pagination metadata for list responses */
    @SerialName("pagination") val pagination: PaginationDto? = null,
    /** Debug information (only in development mode) */
    @SerialName("debug") val debug: JsonObject? = null,
)

/**
 * Pagination metadata for list responses.
 */
@Serializable
data class PaginationDto(
    /** Total number of items matching the query */
    @SerialName("total") val total: Int,
    /** Maximum items per page */
    @SerialName("limit") val limit: Int,
    /** Current offset from start */
    @SerialName("offset") val offset: Int,
    /** Whether more items are available */
    @SerialName("hasMore") val hasMore: Boolean,
)

/**
 * Minimal success payload used by endpoints that only acknowledge completion.
 */
@Serializable
data class SuccessResponse(
    @SerialName("message") val message: String? = null,
)
