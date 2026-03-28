package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
data class CreateTagRequestDto(
    @SerialName("name") val name: String,
    @SerialName("color") val color: String? = null,
)

@Serializable
data class UpdateTagRequestDto(
    @SerialName("name") val name: String? = null,
    @SerialName("color") val color: String? = null,
)

@Serializable
data class MergeTagsRequestDto(
    @SerialName("source_tag_ids") val sourceTagIds: List<Int>,
    @SerialName("target_tag_id") val targetTagId: Int,
)

@Serializable
data class AttachTagsRequestDto(
    @SerialName("tag_ids") val tagIds: List<Int>? = null,
    @SerialName("tag_names") val tagNames: List<String>? = null,
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
