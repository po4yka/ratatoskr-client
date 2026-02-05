package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

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
 */
@Serializable
data class SyncSessionResponseDto(
    /** Sync session identifier */
    @SerialName("session_id") val sessionId: String,
    /** Session expiration time */
    @SerialName("expires_at") val expiresAt: String? = null,
    /** Total items available for sync */
    @SerialName("total_items") val totalItems: Int? = null,
)

/**
 * Full sync response data (chunked).
 */
@Serializable
data class FullSyncResponseDto(
    /** Synced items in this chunk */
    @SerialName("items") val items: List<SyncItemDto> = emptyList(),
    /** Whether more chunks are available */
    @SerialName("has_more") val hasMore: Boolean = false,
    /** Cursor for fetching next chunk */
    @SerialName("next_cursor") val nextCursor: Long? = null,
    /** Current server version */
    @SerialName("server_version") val serverVersion: Long? = null,
)

/**
 * Delta sync response data.
 */
@Serializable
data class DeltaSyncResponseDto(
    /** Created items since cursor */
    @SerialName("created") val created: List<SyncItemDto> = emptyList(),
    /** Updated items since cursor */
    @SerialName("updated") val updated: List<SyncItemDto> = emptyList(),
    /** Deleted item IDs since cursor */
    @SerialName("deleted") val deleted: List<Long> = emptyList(),
    /** Whether more changes are available */
    @SerialName("has_more") val hasMore: Boolean = false,
    /** New cursor for subsequent delta sync */
    @SerialName("new_cursor") val newCursor: Long? = null,
    /** Current server version */
    @SerialName("server_version") val serverVersion: Long? = null,
)

/**
 * Individual sync item.
 * Note: The server sends entity-specific data under keys matching the entity type
 * (e.g., "summary" for summary entities). The `summary` field contains the actual data.
 */
@Serializable
data class SyncItemDto(
    @SerialName("id") val id: Long,
    @SerialName("entityType") val entityType: String, // "summary", "collection", etc.
    @SerialName("serverVersion") val serverVersion: Long,
    /** Summary data - populated when entityType is "summary" */
    @SerialName("summary") val summary: JsonObject? = null,
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null,
    @SerialName("deletedAt") val deletedAt: String? = null,
)

// ============================================================================
// Sync Apply DTOs (for pushing local changes)
// ============================================================================

/**
 * Request to apply local changes to server.
 */
@Serializable
data class SyncApplyRequestDto(
    /** Sync session identifier */
    @SerialName("session_id") val sessionId: String,
    /** List of changes to apply */
    @SerialName("changes") val changes: List<SyncApplyItemDto>,
)

/**
 * Individual change item to apply.
 */
@Serializable
data class SyncApplyItemDto(
    /** Entity type (e.g., "summary", "collection") */
    @SerialName("entity_type") val entityType: String,
    /** Entity ID */
    @SerialName("id") val id: Long,
    /** Action to perform: "create", "update", "delete" */
    @SerialName("action") val action: String,
    /** Last seen server version for conflict detection */
    @SerialName("last_seen_version") val lastSeenVersion: Long,
    /** Entity payload for create/update (not needed for delete) */
    @SerialName("payload") val payload: JsonObject? = null,
    /** Client-side timestamp of the change */
    @SerialName("client_timestamp") val clientTimestamp: String? = null,
)

/**
 * Result of applying changes.
 */
@Serializable
data class SyncApplyResponseDto(
    /** Successfully applied changes */
    @SerialName("applied") val applied: List<SyncApplyResultDto> = emptyList(),
    /** Conflicting changes that weren't applied */
    @SerialName("conflicts") val conflicts: List<SyncConflictDto> = emptyList(),
    /** New server version after apply */
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
    /** Server's current data (for manual conflict resolution) */
    @SerialName("server_payload") val serverPayload: JsonObject? = null,
)
