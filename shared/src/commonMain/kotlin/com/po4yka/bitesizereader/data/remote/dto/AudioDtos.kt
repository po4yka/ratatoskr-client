package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * TTS audio generation response matching backend POST /v1/summaries/{id}/audio.
 * Note: Backend returns camelCase for this endpoint.
 */
@Serializable
data class GenerateAudioResponseDto(
    @SerialName("summaryId") val summaryId: Long,
    @SerialName("status") val status: String,
    @SerialName("charCount") val charCount: Int? = null,
    @SerialName("fileSizeBytes") val fileSizeBytes: Long? = null,
    @SerialName("latencyMs") val latencyMs: Int? = null,
    @SerialName("error") val error: String? = null,
)
