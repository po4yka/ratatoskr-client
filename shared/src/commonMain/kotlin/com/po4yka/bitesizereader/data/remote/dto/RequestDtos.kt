package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubmitURLRequestDto(
    @SerialName("input_url") val inputUrl: String,
    @SerialName("lang_preference") val langPreference: String = "auto",
    @SerialName("type") val type: String = "url",
)

@Serializable
data class SubmitRequestResponseDto(
    @SerialName("request_id") val requestId: Long,
    @SerialName("correlation_id") val correlationId: String,
    @SerialName("type") val type: String,
    @SerialName("status") val status: String,
    @SerialName("estimated_wait_seconds") val estimatedWaitSeconds: Int? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("is_duplicate") val isDuplicate: Boolean,
    @SerialName("duplicate_request_id") val duplicateRequestId: Long? = null,
    @SerialName("duplicate_summary_id") val duplicateSummaryId: Long? = null,
    @SerialName("duplicate_summary") val duplicateSummary: SummaryCompactDto? = null,
)

@Serializable
data class RequestStatusResponseDto(
    @SerialName("request_id") val requestId: Long,
    @SerialName("status") val status: String,
    @SerialName("stage") val stage: String? = null,
    @SerialName("estimated_seconds_remaining") val estimatedSecondsRemaining: Int? = null,
    @SerialName("error_stage") val errorStage: String? = null,
    @SerialName("error_type") val errorType: String? = null,
    @SerialName("error_message") val errorMessage: String? = null,
    @SerialName("can_retry") val canRetry: Boolean? = null,
    @SerialName("correlation_id") val correlationId: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
data class CrawlResultDto(
    @SerialName("status") val status: String? = null,
    @SerialName("http_status") val httpStatus: Int? = null,
    @SerialName("latency_ms") val latencyMs: Int? = null,
    @SerialName("error") val error: String? = null
)

@Serializable
data class LlmCallDto(
    @SerialName("id") val id: Long,
    @SerialName("model") val model: String,
    @SerialName("status") val status: String,
    @SerialName("tokens_prompt") val tokensPrompt: Int? = null,
    @SerialName("tokens_completion") val tokensCompletion: Int? = null,
    @SerialName("cost_usd") val costUsd: Double? = null,
    @SerialName("latency_ms") val latencyMs: Int? = null,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class SummarySimpleDto(
    @SerialName("id") val id: Long,
    @SerialName("status") val status: String,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class RequestDetailDto(
    @SerialName("request") val request: RequestInfoDto,
    @SerialName("crawl_result") val crawlResult: CrawlResultDto? = null,
    @SerialName("llm_calls") val llmCalls: List<LlmCallDto> = emptyList(),
    @SerialName("summary") val summary: SummarySimpleDto? = null
)

@Serializable
data class RetryRequestResponseDto(
    @SerialName("new_request_id") val newRequestId: Long,
    @SerialName("correlation_id") val correlationId: String,
    @SerialName("status") val status: String,
    @SerialName("created_at") val createdAt: String
)
