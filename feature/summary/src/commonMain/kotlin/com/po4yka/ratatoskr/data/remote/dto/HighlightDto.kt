package com.po4yka.ratatoskr.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Highlight sync payload owned by `feature/summary` — the only consumer is
 * the summary feature's `HighlightSyncItemApplier`.
 */
@Serializable
data class HighlightDto(
    val id: String,
    @SerialName("summary_id") val summaryId: String,
    val text: String,
    @SerialName("start_offset") val startOffset: Int? = null,
    @SerialName("end_offset") val endOffset: Int? = null,
    val color: String? = null,
    val note: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
)
