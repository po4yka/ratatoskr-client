package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DuplicateUrlCheckDataDto(
    @SerialName("is_duplicate") val isDuplicate: Boolean,
    @SerialName("normalized_url") val normalizedUrl: String,
    @SerialName("dedupe_hash") val dedupeHash: String,
    @SerialName("request_id") val requestId: Long? = null,
    @SerialName("summary_id") val summaryId: Long? = null,
    @SerialName("summarized_at") val summarizedAt: String? = null,
    @SerialName("summary") val summary: SummaryCompactDto? = null,
)

@Serializable
data class DuplicateUrlCheckResponseEnvelope(
    @SerialName("success") val success: Boolean,
    @SerialName("data") val data: DuplicateUrlCheckDataDto,
)
