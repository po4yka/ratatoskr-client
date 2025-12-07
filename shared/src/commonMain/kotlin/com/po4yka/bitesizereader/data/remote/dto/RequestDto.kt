package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestCreatedDto(
    @SerialName("request_id") val requestId: Long? = null,
    @SerialName("correlation_id") val correlationId: String? = null,
    @SerialName("type") val type: String,
    @SerialName("status") val status: String,
    @SerialName("estimated_wait_seconds") val estimatedWaitSeconds: Int? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("is_duplicate") val isDuplicate: Boolean,
    @SerialName("existing_request_id") val existingRequestId: Long? = null,
    @SerialName("existing_summary_id") val existingSummaryId: Long? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("summarized_at") val summarizedAt: String? = null
)
