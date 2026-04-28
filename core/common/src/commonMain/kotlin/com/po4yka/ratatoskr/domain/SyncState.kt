package com.po4yka.ratatoskr.domain.model

import kotlin.time.Instant

data class SyncState(
    val lastSyncTime: Instant?,
    val lastSyncHash: String?,
)

/**
 * Represents the current phase of a sync operation.
 */
enum class SyncPhase {
    /** Creating a sync session with the server */
    CREATING_SESSION,

    /** Fetching all items (full sync) */
    FETCHING_FULL,

    /** Fetching changes since last sync (delta sync) */
    FETCHING_DELTA,

    /** Processing received items */
    PROCESSING,

    /** Validating sync integrity */
    VALIDATING,

    /** Sync completed successfully */
    COMPLETED,

    /** Sync failed with error */
    FAILED,

    /** Sync was cancelled by user */
    CANCELLED,
}

/**
 * Tracks the progress of an ongoing sync operation.
 *
 * @property phase Current phase of the sync operation
 * @property totalItems Total number of items to sync (null if unknown)
 * @property processedItems Number of items successfully processed
 * @property currentBatch Current batch number being processed
 * @property totalBatches Total number of batches (null if unknown)
 * @property errorCount Number of items that failed to process
 * @property startTime When the sync operation started
 * @property errorMessage Error message if sync failed
 */
data class SyncProgress(
    val phase: SyncPhase,
    val totalItems: Int? = null,
    val processedItems: Int = 0,
    val currentBatch: Int = 0,
    val totalBatches: Int? = null,
    val errorCount: Int = 0,
    val startTime: Instant,
    val errorMessage: String? = null,
) {
    /**
     * Progress percentage (0.0 to 1.0), or null if total is unknown.
     */
    val progressFraction: Float?
        get() =
            totalItems?.let { total ->
                if (total > 0) processedItems.toFloat() / total else 1f
            }

    /**
     * Whether the sync operation is still in progress.
     */
    val isInProgress: Boolean
        get() = phase !in listOf(SyncPhase.COMPLETED, SyncPhase.FAILED, SyncPhase.CANCELLED)
}

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
