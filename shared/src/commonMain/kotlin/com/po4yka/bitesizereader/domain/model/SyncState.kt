@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.po4yka.bitesizereader.domain.model

import kotlinx.datetime.Instant

/**
 * Domain model representing synchronization state.
 */
sealed class SyncState {
    object Idle : SyncState()

    data class Syncing(
        val type: SyncType,
        val progress: Int, // 0-100
        val currentStep: String,
    ) : SyncState()

    data class Success(
        val type: SyncType,
        val syncedAt: Instant,
        val itemsSynced: Int,
    ) : SyncState()

    data class Error(
        val type: SyncType,
        val error: String,
        val canRetry: Boolean,
    ) : SyncState()

    data class Retrying(
        val type: SyncType,
        val attemptNumber: Int,
        val maxAttempts: Int,
    ) : SyncState()
}

enum class SyncType {
    FULL,
    DELTA,
    UPLOAD,
}

/**
 * Metadata for tracking sync state
 */
data class SyncMetadata(
    val lastFullSync: Instant? = null,
    val lastDeltaSync: Instant? = null,
    val lastSyncTimestamp: Instant? = null,
    val deviceId: String,
    val pendingChanges: Int = 0,
)
