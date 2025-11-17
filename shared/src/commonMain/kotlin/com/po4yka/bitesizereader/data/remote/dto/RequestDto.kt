package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Submit URL request DTO
 */
@Serializable
data class SubmitURLRequestDto(
    val type: String = "url",
    @SerialName("input_url") val inputUrl: String,
    @SerialName("lang_preference") val langPreference: String = "auto",
)

/**
 * Request response DTO
 */
@Serializable
data class RequestResponseDto(
    @SerialName("request_id") val requestId: Int,
    val status: String,
    val stage: String? = null,
    val progress: Int,
    @SerialName("created_at") val createdAt: String,
)

/**
 * Request status DTO
 */
@Serializable
data class RequestStatusDto(
    @SerialName("request_id") val requestId: Int,
    val status: String,
    val stage: String? = null,
    val progress: Int,
    @SerialName("estimated_seconds_remaining") val estimatedSecondsRemaining: Int? = null,
    @SerialName("error_message") val errorMessage: String? = null,
    @SerialName("can_retry") val canRetry: Boolean = false,
    @SerialName("summary_id") val summaryId: Int? = null,
)
