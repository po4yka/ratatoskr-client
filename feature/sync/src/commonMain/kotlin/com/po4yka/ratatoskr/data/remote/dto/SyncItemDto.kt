package com.po4yka.ratatoskr.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.longOrNull

/**
 * Sync transport envelope owned by `feature/sync`. Other features consume the
 * `feature.sync.api.SyncEntity` projection instead — this DTO stays internal
 * to the sync module's data layer.
 */
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
