package com.po4yka.ratatoskr.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class RequestInfoDto(
    @SerialName("id") val id: Long,
    @SerialName("type") val type: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("inputUrl") val inputUrl: String? = null,
    @SerialName("normalizedUrl") val normalizedUrl: String? = null,
    @SerialName("correlationId") val correlationId: String? = null,
    @SerialName("createdAt") val createdAt: String? = null,
)

@Serializable
data class SourceInfoDto(
    @SerialName("url") val url: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("domain") val domain: String? = null,
    @SerialName("author") val author: String? = null,
    @SerialName("publishedAt") val publishedAt: String? = null,
    @SerialName("httpStatus") val httpStatus: Int? = null,
    @SerialName("imageUrl") val imageUrl: String? = null,
)

@Serializable
data class ProcessingInfoDto(
    @SerialName("model") val model: String? = null,
    @SerialName("tokensUsed") val tokensUsed: Int? = null,
    @SerialName("costUsd") val costUsd: Double? = null,
    @SerialName("latencyMs") val latencyMs: Int? = null,
    @SerialName("crawlLatencyMs") val crawlLatencyMs: Int? = null,
    @SerialName("llmLatencyMs") val llmLatencyMs: Int? = null,
)

@Serializable
data class SummaryDetailDto(
    @SerialName("id") val id: Long,
    @SerialName("requestId") val requestId: Long,
    @SerialName("lang") val lang: String? = null,
    @SerialName("isRead") val isRead: Boolean = false,
    @SerialName("isFavorited") val isFavorited: Boolean = false,
    @SerialName("version") val version: Int? = null,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("jsonPayload") val jsonPayload: JsonElement? = null,
)

@Serializable
data class SummaryDetailDataDto(
    @SerialName("summary") val summary: SummaryDetailDto,
    @SerialName("request") val request: RequestInfoDto? = null,
    @SerialName("source") val source: SourceInfoDto? = null,
    @SerialName("processing") val processing: ProcessingInfoDto? = null,
)

@Serializable
data class UpdateSummaryRequestDto(
    @SerialName("is_read") val isRead: Boolean? = null,
)

@Serializable
data class UpdateSummaryResponseDto(
    @SerialName("id") val id: Long,
    @SerialName("isRead") val isRead: Boolean? = null,
    @SerialName("updatedAt") val updatedAt: String,
)

/**
 * Wrapper for summary content matching OpenAPI SummaryContentData schema.
 */
@Serializable
data class SummaryContentDataDto(
    @SerialName("content") val content: SummaryContentResponseDto,
)

/**
 * Summary content for offline reading matching OpenAPI SummaryContent schema.
 */
@Serializable
data class SummaryContentResponseDto(
    @SerialName("summaryId") val summaryId: Long,
    @SerialName("format") val format: String,
    @SerialName("content") val content: String,
    @SerialName("contentType") val contentType: String,
    @SerialName("retrievedAt") val retrievedAt: String,
    @SerialName("requestId") val requestId: Long? = null,
    @SerialName("lang") val lang: String? = null,
    @SerialName("sourceUrl") val sourceUrl: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("domain") val domain: String? = null,
    @SerialName("sizeBytes") val sizeBytes: Int? = null,
    @SerialName("checksumSha256") val checksumSha256: String? = null,
)
