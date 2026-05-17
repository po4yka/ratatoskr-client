package com.po4yka.ratatoskr.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Tag sync payloads owned by `feature/collections` — both `SyncTagDto` and
 * `SyncSummaryTagDto` are consumed only by the collections feature's sync
 * appliers (`TagSyncItemApplier`, `SummaryTagSyncItemApplier`).
 */
@Serializable
data class SyncTagDto(
    val id: Int,
    val name: String,
    @SerialName("normalized_name") val normalizedName: String? = null,
    val color: String? = null,
    @SerialName("is_deleted") val isDeleted: Boolean = false,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
)

@Serializable
data class SyncSummaryTagDto(
    val id: Int,
    @SerialName("summary_id") val summaryId: Int,
    @SerialName("tag_id") val tagId: Int,
    val source: String? = null,
    @SerialName("created_at") val createdAt: String,
)
