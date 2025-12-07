package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestStatusDto(
    @SerialName("request_id") val requestId: Long,
    @SerialName("status") val status: String,
    @SerialName("stage") val stage: String? = null,
    @SerialName("estimated_seconds_remaining") val estimatedSecondsRemaining: Int? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)
