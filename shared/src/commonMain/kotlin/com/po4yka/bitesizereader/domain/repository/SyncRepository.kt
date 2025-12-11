package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.SyncConflict
import com.po4yka.bitesizereader.domain.model.SyncResult
import com.po4yka.bitesizereader.domain.model.SyncState
import kotlinx.coroutines.flow.Flow

interface SyncRepository {
    // ========================================================================
    // Legacy Sync (backward compatibility)
    // ========================================================================

    /** Legacy sync using session-id-based delta sync */
    suspend fun sync(forceFull: Boolean = false)

    /** Get current sync state */
    fun getSyncState(): Flow<SyncState>

    // ========================================================================
    // Session-Based Sync (new OpenAPI spec)
    // ========================================================================

    /** Create a new sync session */
    suspend fun createSyncSession(limit: Int? = null): String

    /** Perform a full sync using the session ID */
    suspend fun fullSync(
        sessionId: String,
        limit: Int? = null,
    ): SyncResult

    /** Perform a delta sync using the session ID and since timestamp */
    suspend fun deltaSync(
        sessionId: String,
        since: Long,
        limit: Int? = null,
    ): SyncResult

    /** Apply local changes to the server */
    suspend fun applyChanges(
        sessionId: String,
        changes: List<LocalChange>,
    ): ApplyResult
}

/** Represents a local change to be pushed to the server */
data class LocalChange(
    val entityType: String,
    val id: Long,
    val action: String, // "create", "update", "delete"
    val lastSeenVersion: Long,
    val payload: Map<String, Any?>? = null,
    val clientTimestamp: String? = null,
)

/** Result of applying local changes */
data class ApplyResult(
    val appliedCount: Int,
    val conflicts: List<SyncConflict>,
    val serverVersion: Long?,
)
