package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.longOrNull

/**
 * Request to create a sync session.
 */
@Serializable
data class SyncSessionRequestDto(
    /** Maximum items per chunk (1-500) */
    @SerialName("limit") val limit: Int? = null,
)

/**
 * Sync session response - provides session ID for subsequent sync calls.
 * Backend serializes via Pydantic `SyncSessionData` with camelCase aliases.
 */
@Serializable
data class SyncSessionResponseDto(
    @SerialName("sessionId") val sessionId: String,
    @SerialName("expiresAt") val expiresAt: String? = null,
    @SerialName("defaultLimit") val defaultLimit: Int? = null,
    @SerialName("maxLimit") val maxLimit: Int? = null,
)

/**
 * Full sync response data (chunked).
 * Backend serializes via Pydantic `FullSyncResponseData` with camelCase aliases.
 */
@Serializable
data class FullSyncResponseDto(
    @SerialName("items") val items: List<SyncItemDto> = emptyList(),
    @SerialName("hasMore") val hasMore: Boolean = false,
    @SerialName("nextSince") val nextCursor: Long? = null,
    @SerialName("serverVersion") val serverVersion: Long? = null,
)

/**
 * Delta sync response data.
 * Backend serializes via Pydantic `DeltaSyncResponseData` with camelCase aliases.
 * The `deleted` array contains full `SyncEntityEnvelope` objects (not bare IDs).
 */
@Serializable
data class DeltaSyncResponseDto(
    @SerialName("created") val created: List<SyncItemDto> = emptyList(),
    @SerialName("updated") val updated: List<SyncItemDto> = emptyList(),
    @SerialName("deleted") val deleted: List<SyncItemDto> = emptyList(),
    @SerialName("hasMore") val hasMore: Boolean = false,
    @SerialName("nextSince") val newCursor: Long? = null,
    @SerialName("serverVersion") val serverVersion: Long? = null,
)

/**
 * Individual sync item (maps to backend `SyncEntityEnvelope`).
 *
 * The server sends entity-specific data under keys matching the entity type.
 * Each entity type has its own typed field on the envelope.
 * The `id` field is `JsonPrimitive` because it can be `int` (summaries, tags)
 * or `string` (highlights use UUIDs).
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
    /** ID as String (works for both numeric and UUID IDs). */
    val idAsString: String get() = id.content

    /** ID as Long (returns null for non-numeric IDs like highlight UUIDs). */
    val idAsLong: Long? get() = id.longOrNull
}

/**
 * Typed representation of a highlight payload received during sync.
 * Matches the "highlight" field on the sync envelope for entity_type="highlight".
 * Backend returns raw dicts (not Pydantic aliases), so field names are snake_case.
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

/**
 * Typed representation of a tag payload received during sync.
 * Backend returns raw dicts, so field names are snake_case.
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

/**
 * Typed representation of a summary-tag association received during sync.
 * Backend returns raw dicts, so field names are snake_case.
 */
@Serializable
data class SyncSummaryTagDto(
    val id: Int,
    @SerialName("summary_id") val summaryId: Int,
    @SerialName("tag_id") val tagId: Int,
    val source: String? = null,
    @SerialName("created_at") val createdAt: String,
)

// ============================================================================
// Sync Apply DTOs (for pushing local changes)
// ============================================================================

/**
 * Valid actions for sync apply items. Backend only accepts "update" and "delete" —
 * new entities are created server-side only.
 */
@Serializable
enum class SyncApplyAction {
    @SerialName("update")
    UPDATE,

    @SerialName("delete")
    DELETE,
    ;

    companion object {
        fun fromString(value: String): SyncApplyAction =
            entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Invalid sync apply action: $value")
    }
}

/**
 * Request to apply local changes to server.
 * Backend accepts snake_case field names (Pydantic reads by field name, not alias).
 */
@Serializable
data class SyncApplyRequestDto(
    @SerialName("session_id") val sessionId: String,
    @SerialName("changes") val changes: List<SyncApplyItemDto>,
)

/**
 * Individual change item to apply.
 */
@Serializable
data class SyncApplyItemDto(
    @SerialName("entity_type") val entityType: String,
    @SerialName("id") val id: Long,
    @SerialName("action") val action: SyncApplyAction,
    @SerialName("last_seen_version") val lastSeenVersion: Long,
    @SerialName("payload") val payload: JsonObject? = null,
    @SerialName("client_timestamp") val clientTimestamp: String? = null,
)

/**
 * Result of applying changes.
 */
@Serializable
data class SyncApplyResponseDto(
    @SerialName("applied") val applied: List<SyncApplyResultDto> = emptyList(),
    @SerialName("conflicts") val conflicts: List<SyncConflictDto> = emptyList(),
    @SerialName("server_version") val serverVersion: Long? = null,
)

/**
 * Result of a single applied change.
 */
@Serializable
data class SyncApplyResultDto(
    @SerialName("id") val id: Long,
    @SerialName("entity_type") val entityType: String,
    @SerialName("action") val action: String,
    @SerialName("new_server_version") val newServerVersion: Long,
)

/**
 * Conflict information for rejected changes.
 */
@Serializable
data class SyncConflictDto(
    @SerialName("id") val id: Long,
    @SerialName("entity_type") val entityType: String,
    @SerialName("client_version") val clientVersion: Long,
    @SerialName("server_version") val serverVersion: Long,
    @SerialName("reason") val reason: String,
    @SerialName("server_payload") val serverPayload: JsonObject? = null,
)
