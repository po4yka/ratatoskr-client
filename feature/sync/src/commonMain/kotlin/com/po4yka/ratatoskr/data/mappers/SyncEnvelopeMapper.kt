package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.api.generated.models.SyncApplyResult
import com.po4yka.ratatoskr.api.generated.models.SyncEntityEnvelope
import com.po4yka.ratatoskr.data.remote.dto.SyncItemDto
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private val mapperJson = Json { ignoreUnknownKeys = true }

/**
 * Bridges the generated [SyncEntityEnvelope] into the existing
 * [SyncItemDto] shape that [com.po4yka.ratatoskr.feature.sync.api.SyncItemApplier]
 * implementations consume across features.
 *
 * The generated model's `id` is an empty sealed interface (a known
 * generator limitation around OAS `oneOf [int, string]`), so we round-trip
 * through JSON to recover the underlying primitive. The destination
 * [SyncItemDto] stores it as a [JsonPrimitive] for downstream callers.
 */
internal fun SyncEntityEnvelope.toSyncItemDto(): SyncItemDto {
    val asJson = mapperJson.encodeToJsonElement(this).jsonObject
    val rawId = asJson["id"]?.jsonPrimitive ?: JsonPrimitive("")
    return SyncItemDto(
        id = rawId,
        entityType = entityType.serialName(),
        serverVersion = serverVersion,
        summary = asJson["summary"]?.takeIf { it !is JsonNull }?.jsonObject,
        request = asJson["request"]?.takeIf { it !is JsonNull }?.jsonObject,
        preference = asJson["preference"]?.takeIf { it !is JsonNull }?.jsonObject,
        stat = asJson["stat"]?.takeIf { it !is JsonNull }?.jsonObject,
        crawlResult = null,
        llmCall = null,
        highlight = null,
        tag = null,
        summaryTag = null,
        createdAt = null,
        updatedAt = updatedAt.toString(),
        deletedAt = deletedAt?.toString(),
    )
}

/**
 * Recovers the raw apply-result id as a string, regardless of whether the
 * wire format produced an int or a string (the generated [SyncApplyResult.Id]
 * is an empty sealed interface).
 */
internal fun SyncApplyResult.idAsString(): String {
    val asJson = mapperJson.encodeToJsonElement(this).jsonObject
    return asJson["id"]?.jsonPrimitive?.content ?: ""
}

/**
 * Recovers the apply-result id as a [Long] when the wire value is numeric.
 */
internal fun SyncApplyResult.idAsLong(): Long? = idAsString().toLongOrNull()

private fun SyncEntityEnvelope.EntityType.serialName(): String =
    when (this) {
        SyncEntityEnvelope.EntityType.SUMMARY -> "summary"
        SyncEntityEnvelope.EntityType.REQUEST -> "request"
        SyncEntityEnvelope.EntityType.PREFERENCE -> "preference"
        SyncEntityEnvelope.EntityType.STAT -> "stat"
        SyncEntityEnvelope.EntityType.CRAWL_RESULT -> "crawl_result"
        SyncEntityEnvelope.EntityType.LLM_CALL -> "llm_call"
    }

