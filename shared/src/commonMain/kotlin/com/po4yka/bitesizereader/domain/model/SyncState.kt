package com.po4yka.bitesizereader.domain.model

import kotlin.time.Instant

data class SyncState(
    val lastSyncTime: Instant?,
    val lastSyncHash: String?,
)

/**
 * Result of a sync operation (full or delta).
 */
data class SyncResult(
    /** Number of items created */
    val createdCount: Int,
    /** Number of items updated */
    val updatedCount: Int,
    /** Number of items deleted */
    val deletedCount: Int,
    /** Whether there are more items to sync */
    val hasMore: Boolean,
    /** Cursor for the next sync request */
    val nextCursor: Long?,
    /** Current server version */
    val serverVersion: Long?,
)

/**
 * Represents a conflict detected during sync apply.
 */
data class SyncConflict(
    val id: Long,
    val entityType: String,
    val clientVersion: Long,
    val serverVersion: Long,
    val reason: String,
)
