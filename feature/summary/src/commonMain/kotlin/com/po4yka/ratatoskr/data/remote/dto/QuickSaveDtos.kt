package com.po4yka.ratatoskr.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuickSaveRequestDto(
    @SerialName("url") val url: String,
    @SerialName("title") val title: String? = null,
    @SerialName("selected_text") val selectedText: String? = null,
    @SerialName("tag_names") val tagNames: List<String> = emptyList(),
    @SerialName("summarize") val summarize: Boolean = true,
)

@Serializable
data class QuickSaveResponseDto(
    @SerialName("request_id") val requestId: Long? = null,
    @SerialName("status") val status: String,
    @SerialName("title") val title: String? = null,
    @SerialName("url") val url: String,
    @SerialName("duplicate") val duplicate: Boolean = false,
    @SerialName("summary_id") val summaryId: Long? = null,
    @SerialName("tags_attached") val tagsAttached: List<String>? = null,
)
