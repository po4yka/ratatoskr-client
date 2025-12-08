package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class RequestInfoDto(
    @SerialName("id") val id: Long,
    @SerialName("type") val type: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("input_url") val inputUrl: String? = null,
    @SerialName("normalized_url") val normalizedUrl: String? = null,
    @SerialName("correlation_id") val correlationId: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class SourceInfoDto(
    @SerialName("url") val url: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("domain") val domain: String? = null,
    @SerialName("author") val author: String? = null,
    @SerialName("published_at") val publishedAt: String? = null,
    @SerialName("http_status") val httpStatus: Int? = null
)

@Serializable
data class ProcessingInfoDto(
    @SerialName("model") val model: String? = null,
    @SerialName("tokens_used") val tokensUsed: Int? = null,
    @SerialName("cost_usd") val costUsd: Double? = null,
    @SerialName("latency_ms") val latencyMs: Int? = null,
    @SerialName("crawl_latency_ms") val crawlLatencyMs: Int? = null,
    @SerialName("llm_latency_ms") val llmLatencyMs: Int? = null
)

@Serializable
data class SummaryDetailDto(
    @SerialName("id") val id: Long,
    @SerialName("request_id") val requestId: Long,
    @SerialName("lang") val lang: String? = null,
    @SerialName("is_read") val isRead: Boolean = false,
    @SerialName("version") val version: Int? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("json_payload") val jsonPayload: JsonElement? = null
)

@Serializable
data class SummaryDetailDataDto(
    @SerialName("summary") val summary: SummaryDetailDto,
    @SerialName("request") val request: RequestInfoDto? = null,
    @SerialName("source") val source: SourceInfoDto? = null,
    @SerialName("processing") val processing: ProcessingInfoDto? = null
)

@Serializable
data class UpdateSummaryRequestDto(
    @SerialName("is_read") val isRead: Boolean? = null
)

@Serializable
data class UpdateSummaryResponseDto(
    @SerialName("id") val id: Long,
    @SerialName("is_read") val isRead: Boolean? = null,
    @SerialName("updated_at") val updatedAt: String
)
