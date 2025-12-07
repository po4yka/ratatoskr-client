package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponseDto(
    @SerialName("code") val code: String,
    @SerialName("message") val message: String,
    @SerialName("correlation_id") val correlationId: String? = null,
    @SerialName("retry_after") val retryAfter: Int? = null
)
