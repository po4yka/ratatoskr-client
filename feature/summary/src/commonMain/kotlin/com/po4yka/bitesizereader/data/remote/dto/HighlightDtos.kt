package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateHighlightRequestDto(
    @SerialName("text") val text: String,
    @SerialName("start_offset") val startOffset: Int? = null,
    @SerialName("end_offset") val endOffset: Int? = null,
    @SerialName("color") val color: String? = null,
    @SerialName("note") val note: String? = null,
)

@Serializable
data class UpdateHighlightRequestDto(
    @SerialName("color") val color: String? = null,
    @SerialName("note") val note: String? = null,
)

@Serializable
data class HighlightResponseDto(
    @SerialName("id") val id: String,
    @SerialName("summaryId") val summaryId: String,
    @SerialName("text") val text: String,
    @SerialName("startOffset") val startOffset: Int? = null,
    @SerialName("endOffset") val endOffset: Int? = null,
    @SerialName("color") val color: String? = null,
    @SerialName("note") val note: String? = null,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String,
)

@Serializable
data class HighlightListResponseDto(
    @SerialName("highlights") val highlights: List<HighlightResponseDto>,
)

@Serializable
data class HighlightDeleteResponseDto(
    @SerialName("deleted") val deleted: Boolean,
    @SerialName("id") val id: String,
)
