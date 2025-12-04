package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SyncDeltaResponseDto(
    @SerialName("upserted_summaries") val upsertedSummaries: List<SummaryDto>,
    @SerialName("deleted_summary_ids") val deletedSummaryIds: List<String>,
    @SerialName("sync_token") val syncToken: String
)
