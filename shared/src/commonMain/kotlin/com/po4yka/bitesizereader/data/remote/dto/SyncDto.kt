package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Sync delta response DTO
 */
@Serializable
data class SyncDeltaResponseDto(
    val summaries: List<SyncChangeDto>,
    @SerialName("deleted_ids") val deletedIds: List<Int>,
    @SerialName("sync_timestamp") val syncTimestamp: String,
)

/**
 * Sync change DTO
 */
@Serializable
data class SyncChangeDto(
    val id: Int,
    val action: String, // "update" or "delete"
    val data: SummaryDetailDto? = null,
)

/**
 * Sync upload request DTO
 */
@Serializable
data class SyncUploadRequestDto(
    val changes: List<SyncUploadChangeDto>,
    @SerialName("device_id") val deviceId: String,
    @SerialName("last_sync") val lastSync: String,
)

/**
 * Sync upload change DTO
 */
@Serializable
data class SyncUploadChangeDto(
    @SerialName("summary_id") val summaryId: Int,
    val action: String,
    val fields: Map<String, @Contextual Any>,
    @SerialName("client_timestamp") val clientTimestamp: String,
)
