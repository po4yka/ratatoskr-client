package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.longOrNull

@Serializable
data class SyncItemDto(
    @SerialName("id") val id: JsonPrimitive,
    @SerialName("entityType") val entityType: String,
    @SerialName("serverVersion") val serverVersion: Long = 0,
    @SerialName("summary") val summary: JsonObject? = null,
    @SerialName("request") val request: JsonObject? = null,
    @SerialName("preference") val preference: JsonObject? = null,
    @SerialName("stat") val stat: JsonObject? = null,
    @SerialName("crawlResult") val crawlResult: JsonObject? = null,
    @SerialName("llmCall") val llmCall: JsonObject? = null,
    @SerialName("highlight") val highlight: JsonObject? = null,
    @SerialName("tag") val tag: JsonObject? = null,
    @SerialName("summaryTag") val summaryTag: JsonObject? = null,
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null,
    @SerialName("deletedAt") val deletedAt: String? = null,
) {
    val idAsString: String get() = id.content
    val idAsLong: Long? get() = id.longOrNull
}

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
