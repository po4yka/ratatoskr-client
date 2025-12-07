package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class SyncCreatedItemDto(
    @SerialName("summary_id") val summaryId: Long,
    @SerialName("created_at") val createdAt: String,
    @SerialName("data") val data: SyncSummaryDataDto
)

@Serializable
data class SyncSummaryDataDto(
    @SerialName("id") val id: Long,
    @SerialName("request_id") val requestId: Long,
    @SerialName("json_payload") val jsonPayload: Map<String, JsonElement>? = null,
    @SerialName("is_read") val isRead: Boolean = false,
    @SerialName("lang") val lang: String? = null
)

@Serializable
data class SyncChangesDto(
    @SerialName("created") val created: List<SyncCreatedItemDto> = emptyList(),
    @SerialName("updated") val updated: List<SyncCreatedItemDto> = emptyList(),
    @SerialName("deleted") val deleted: List<Long> = emptyList()
)

@Serializable
data class SyncDeltaResponseDto(
    @SerialName("changes") val changes: SyncChangesDto,
    @SerialName("sync_timestamp") val syncTimestamp: String,
    @SerialName("has_more") val hasMore: Boolean
)
