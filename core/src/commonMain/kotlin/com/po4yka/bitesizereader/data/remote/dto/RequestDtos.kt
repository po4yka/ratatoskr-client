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
    @SerialName("requestId") val requestId: Long,
    @SerialName("correlationId") val correlationId: String,
    @SerialName("type") val type: String,
    @SerialName("status") val status: String,
    @SerialName("estimatedWaitSeconds") val estimatedWaitSeconds: Int? = null,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("isDuplicate") val isDuplicate: Boolean,
    @SerialName("duplicate_request_id") val duplicateRequestId: Long? = null,
    @SerialName("duplicate_summary_id") val duplicateSummaryId: Long? = null,
    @SerialName("duplicate_summary") val duplicateSummary: SummaryCompactDto? = null,
)

/**
 * Request status response matching OpenAPI RequestStatusData schema.
 */
@Serializable
data class RequestStatusResponseDto(
    @SerialName("requestId") val requestId: Long,
    @SerialName("status") val status: String,
    @SerialName("stage") val stage: String? = null,
    /** Processing progress details */
    @SerialName("progress") val progress: ProgressDto? = null,
    @SerialName("estimatedSecondsRemaining") val estimatedSecondsRemaining: Int? = null,
    @SerialName("errorStage") val errorStage: String? = null,
    @SerialName("errorType") val errorType: String? = null,
    @SerialName("errorMessage") val errorMessage: String? = null,
    @SerialName("canRetry") val canRetry: Boolean? = null,
    @SerialName("correlationId") val correlationId: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null,
    @SerialName("queuePosition") val queuePosition: Int? = null,
)

/**
 * Processing progress information.
 */
@Serializable
data class ProgressDto(
    @SerialName("current_step") val currentStep: Int,
    @SerialName("total_steps") val totalSteps: Int,
    @SerialName("percentage") val percentage: Int,
)

@Serializable
data class CrawlResultDto(
    @SerialName("status") val status: String? = null,
    @SerialName("httpStatus") val httpStatus: Int? = null,
    @SerialName("latencyMs") val latencyMs: Int? = null,
    @SerialName("error") val error: String? = null,
)

@Serializable
data class LlmCallDto(
    @SerialName("id") val id: Long,
    @SerialName("model") val model: String,
    @SerialName("status") val status: String,
    @SerialName("tokensPrompt") val tokensPrompt: Int? = null,
    @SerialName("tokensCompletion") val tokensCompletion: Int? = null,
    @SerialName("costUsd") val costUsd: Double? = null,
    @SerialName("latencyMs") val latencyMs: Int? = null,
    @SerialName("createdAt") val createdAt: String,
)

@Serializable
data class SummarySimpleDto(
    @SerialName("id") val id: Long,
    @SerialName("status") val status: String,
    @SerialName("createdAt") val createdAt: String,
)

@Serializable
data class RequestDetailDto(
    @SerialName("request") val request: RequestInfoDto,
    @SerialName("crawlResult") val crawlResult: CrawlResultDto? = null,
    @SerialName("llmCalls") val llmCalls: List<LlmCallDto> = emptyList(),
    @SerialName("summary") val summary: SummarySimpleDto? = null,
)

/**
 * Forward message submission matching backend SubmitForwardRequest schema.
 */
@Serializable
data class SubmitForwardRequestDto(
    @SerialName("content_text") val contentText: String,
    @SerialName("forward_metadata") val forwardMetadata: ForwardMetadataDto? = null,
    @SerialName("lang_preference") val langPreference: String = "auto",
    @SerialName("type") val type: String = "forward",
)

@Serializable
data class ForwardMetadataDto(
    @SerialName("from_chat_id") val fromChatId: Long,
    @SerialName("from_message_id") val fromMessageId: Long,
    @SerialName("from_chat_title") val fromChatTitle: String? = null,
    @SerialName("forwarded_at") val forwardedAt: String? = null,
)

@Serializable
data class RetryRequestResponseDto(
    @SerialName("newRequestId") val newRequestId: Long,
    @SerialName("correlationId") val correlationId: String,
    @SerialName("status") val status: String,
    @SerialName("createdAt") val createdAt: String,
)
