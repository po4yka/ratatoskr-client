package com.po4yka.ratatoskr.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response shape for `/v1/tags` endpoints — kept hand-written because the
 * generated `TagsApi` returns `JsonElement` (the OpenAPI spec lacks a
 * concrete response schema for tag endpoints).
 */
@Serializable
data class TagDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("color") val color: String? = null,
    @SerialName("summaryCount") val summaryCount: Int = 0,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String,
)

@Serializable
data class TagListResponseDto(
    @SerialName("tags") val tags: List<TagDto>,
)

@Serializable
data class TagDeleteResponseDto(
    @SerialName("deleted") val deleted: Boolean,
    @SerialName("id") val id: Int,
)

@Serializable
data class TagMergeResponseDto(
    @SerialName("merged") val merged: Boolean,
    @SerialName("target_tag_id") val targetTagId: Int,
)

@Serializable
data class TagDetachResponseDto(
    @SerialName("detached") val detached: Boolean,
    @SerialName("summary_id") val summaryId: Int,
    @SerialName("tag_id") val tagId: Int,
)
