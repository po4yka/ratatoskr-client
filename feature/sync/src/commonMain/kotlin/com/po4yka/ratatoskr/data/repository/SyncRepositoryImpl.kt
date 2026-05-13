package com.po4yka.ratatoskr.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.po4yka.ratatoskr.api.generated.api.SyncApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.SyncApplyResult
import com.po4yka.ratatoskr.api.generated.models.SyncEntityEnvelope
import com.po4yka.ratatoskr.data.mappers.buildSyncApplyRequest
import com.po4yka.ratatoskr.data.mappers.idAsLong
import com.po4yka.ratatoskr.data.mappers.idAsString
import com.po4yka.ratatoskr.data.mappers.toSyncItemDto
import com.po4yka.ratatoskr.database.Database
import com.po4yka.ratatoskr.domain.model.SyncConflict
import com.po4yka.ratatoskr.domain.model.SyncPhase
import com.po4yka.ratatoskr.domain.model.SyncProgress
import com.po4yka.ratatoskr.domain.model.SyncResult
import com.po4yka.ratatoskr.domain.model.SyncState
import com.po4yka.ratatoskr.feature.sync.api.PendingOperationHandler
import com.po4yka.ratatoskr.feature.sync.api.SyncItemApplier
import com.po4yka.ratatoskr.feature.sync.domain.repository.ApplyResult
import com.po4yka.ratatoskr.feature.sync.domain.repository.LocalChange
import com.po4yka.ratatoskr.feature.sync.domain.repository.SyncRepository
import com.po4yka.ratatoskr.util.network.NetworkMonitor
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withTimeout
import kotlin.concurrent.Volatile
import kotlin.coroutines.coroutineContext
import kotlin.time.Clock
import kotlin.time.Instant

private val logger = KotlinLogging.logger {}

/** Default batch size for sync operations */
private const val DEFAULT_BATCH_SIZE = 100

/** Sub-chunk size for database transactions within a batch */
private const val TRANSACTION_CHUNK_SIZE = 25

/** Timeout for the entire sync operation (5 minutes) */
private const val SYNC_TIMEOUT_MS = 5 * 60 * 1000L

class SyncRepositoryImpl(
    private val database: Database,
    private val networkMonitor: NetworkMonitor,
    syncItemAppliers: List<SyncItemApplier>,
    pendingOperationHandlers: List<PendingOperationHandler>,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : SyncRepository {
    private val sessionCoordinator = SyncSessionCoordinator()

    private val syncItemApplierRegistry =
        SyncItemApplierRegistry(appliers = syncItemAppliers)

    private val pendingOperationFlusher =
        PendingOperationFlusher(
            database = database,
            handlers = pendingOperationHandlers,
            applyChanges = ::applyChanges,
            onConflictCount = { conflicts ->
                _syncProgress.update { current ->
                    current?.copy(errorCount = (current.errorCount) + conflicts)
                }
            },
        )

    // ========================================================================
    // Progress Tracking
    // ========================================================================

    private val _syncProgress = MutableStateFlow<SyncProgress?>(null)
    override val syncProgress: StateFlow<SyncProgress?> = _syncProgress.asStateFlow()

    // @Volatile ensures visibility across threads. cancelSync() reads currentSyncJob without
    // the mutex, so volatile guarantees it sees the latest value written by sync().
    // fullSyncReceivedIds is only mutated under syncMutex but read inside fullSync() which
    // runs on a potentially different dispatcher, so volatile ensures visibility.
    @Volatile
    private var currentSyncJob: Job? = null
    private val syncMutex = Mutex()

    @Volatile
    private var fullSyncReceivedIds: MutableSet<String>? = null

    override fun cancelSync() {
        currentSyncJob?.cancel()
        _syncProgress.update { current ->
            current?.copy(phase = SyncPhase.CANCELLED)
        }
        logger.info { "Sync cancelled by user" }
    }

    /**
     * Updates sync progress state. MutableStateFlow is thread-safe, so this can be called
     * from any dispatcher (including IO) without needing to switch to Main.
     */
    private fun updateProgress(
        phase: SyncPhase,
        totalItems: Int? = _syncProgress.value?.totalItems,
        processedItems: Int = _syncProgress.value?.processedItems ?: 0,
        currentBatch: Int = _syncProgress.value?.currentBatch ?: 0,
        totalBatches: Int? = _syncProgress.value?.totalBatches,
        errorCount: Int = _syncProgress.value?.errorCount ?: 0,
        errorMessage: String? = null,
    ) {
        _syncProgress.update { current ->
            SyncProgress(
                phase = phase,
                totalItems = totalItems,
                processedItems = processedItems,
                currentBatch = currentBatch,
                totalBatches = totalBatches,
                errorCount = errorCount,
                startTime = current?.startTime ?: Clock.System.now(),
                errorMessage = errorMessage,
            )
        }
    }

    // ========================================================================
    // Main Sync Entry Point
    // ========================================================================

    override suspend fun sync(forceFull: Boolean) {
        if (!networkMonitor.isConnected()) {
            logger.info { "Sync skipped: no network connection" }
            return
        }
        // tryLock() returns false if already locked -> we return before entering try.
        // This ensures unlock() in finally only runs when we actually acquired the lock.
        if (!syncMutex.tryLock()) {
            logger.info { "Sync already in progress, skipping" }
            return
        }
        try {
            coroutineScope {
                currentSyncJob = coroutineContext[Job]
                withTimeout(SYNC_TIMEOUT_MS) {
                    syncWithSessionBasedEndpoint(forceFull)
                }
            }
        } catch (e: TimeoutCancellationException) {
            logger.error { "Sync operation timed out after ${SYNC_TIMEOUT_MS / 1000}s" }
            updateProgress(phase = SyncPhase.FAILED, errorMessage = "Sync timed out")
            throw e
        } catch (e: CancellationException) {
            updateProgress(phase = SyncPhase.CANCELLED)
            throw e
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            updateProgress(phase = SyncPhase.FAILED, errorMessage = e.message)
            throw e
        } finally {
            // Defensive cleanup: ensure these are always cleared even if performFullSync
            // did not reach its own finally block (e.g., CancellationException in outer scope)
            fullSyncReceivedIds = null
            currentSyncJob = null
            syncMutex.unlock()
        }
    }

    // ========================================================================
    // Session-Based Sync (new approach for all login types)
    // ========================================================================

    private suspend fun syncWithSessionBasedEndpoint(forceFull: Boolean) {
        logger.info { "Starting session-based sync, forceFull=$forceFull" }
        _syncProgress.update {
            SyncProgress(
                phase = SyncPhase.CREATING_SESSION,
                startTime = Clock.System.now(),
            )
        }

        coroutineContext.ensureActive()
        val session = sessionCoordinator.create()

        // Flush pending operations before sync
        pendingOperationFlusher.flush(session.sessionId)

        val metadata = database.databaseQueries.getSyncMetadata().executeAsOneOrNull()
        val lastSyncCursor = metadata?.syncToken?.toLongOrNull() ?: 0L
        val fullSyncInProgress = metadata?.fullSyncInProgress == 1L

        if (forceFull || lastSyncCursor == 0L || fullSyncInProgress) {
            // Resume from checkpoint if a previous full sync was interrupted
            val resumeCursor = if (fullSyncInProgress && lastSyncCursor > 0L) lastSyncCursor else null
            if (resumeCursor != null) {
                logger.info { "Resuming interrupted full sync from cursor $resumeCursor" }
            }
            performFullSync(session.sessionId, session.totalItems, session.expiresAt, resumeCursor)
        } else {
            performDeltaSync(session.sessionId, lastSyncCursor, session.expiresAt)
        }

        updateProgress(phase = SyncPhase.COMPLETED)
        logger.info { "Sync completed successfully" }
    }

    private suspend fun performFullSync(
        initialSessionId: String,
        totalItems: Int?,
        initialSessionExpiresAt: Instant?,
        resumeCursor: Long? = null,
    ) {
        updateProgress(phase = SyncPhase.FETCHING_FULL)

        database.databaseQueries.setFullSyncInProgress(1)

        val receivedIds = mutableSetOf<String>()
        fullSyncReceivedIds = receivedIds

        var sessionId = initialSessionId
        var sessionExpiresAt = initialSessionExpiresAt
        var hasMore = true
        var totalCreated = 0
        val totalErrors = 0
        var cursor: Long? = resumeCursor
        var previousCursor: Long?
        var batchNumber = 0
        var observedCompleteDataset = false
        val totalBatches = totalItems?.let { (it + DEFAULT_BATCH_SIZE - 1) / DEFAULT_BATCH_SIZE }

        try {
            coroutineContext.ensureActive()
            val renewed = sessionCoordinator.renew(sessionId, sessionExpiresAt)
            sessionId = renewed.first
            sessionExpiresAt = renewed.second
            val firstResult = fullSync(sessionId, limit = DEFAULT_BATCH_SIZE, cursor = cursor)

            totalCreated += firstResult.createdCount
            hasMore = firstResult.hasMore
            previousCursor = cursor
            cursor = firstResult.nextCursor
            batchNumber++

            // Guard: break if server says hasMore but provides no cursor to advance
            if (hasMore && cursor == null) {
                throw IllegalStateException("Full sync response has hasMore=true without a next cursor")
            } else if (!hasMore) {
                observedCompleteDataset = true
            }

            updateProgress(
                phase = SyncPhase.FETCHING_FULL,
                totalItems = totalItems,
                processedItems = totalCreated,
                currentBatch = batchNumber,
                totalBatches = totalBatches,
            )

            while (hasMore) {
                coroutineContext.ensureActive()
                val renewedInLoop = sessionCoordinator.renew(sessionId, sessionExpiresAt)
                sessionId = renewedInLoop.first
                sessionExpiresAt = renewedInLoop.second

                val result = fullSync(sessionId, limit = DEFAULT_BATCH_SIZE, cursor = cursor)
                totalCreated += result.createdCount
                hasMore = result.hasMore
                previousCursor = cursor
                cursor = result.nextCursor
                batchNumber++

                // Guard: break if cursor did not advance (prevents infinite loop)
                if (hasMore && (cursor == null || cursor == previousCursor)) {
                    throw IllegalStateException(
                        "Full sync cursor did not advance (was=$previousCursor, now=$cursor)",
                    )
                } else if (!hasMore) {
                    observedCompleteDataset = true
                }

                updateProgress(
                    phase = SyncPhase.FETCHING_FULL,
                    processedItems = totalCreated,
                    currentBatch = batchNumber,
                    errorCount = totalErrors,
                )

                if (hasMore) {
                    logger.info {
                        "Full sync batch $batchNumber complete, hasMore=$hasMore, nextCursor=$cursor"
                    }
                }
            }

            if (shouldCleanupStaleSummariesAfterFullSync(resumeCursor, observedCompleteDataset)) {
                // Remove stale summaries not present on server
                cleanupStaleSummaries(receivedIds)
            } else {
                logger.info {
                    "Skipping stale summary cleanup after full sync: " +
                        "resumeCursor=$resumeCursor, observedCompleteDataset=$observedCompleteDataset"
                }
            }

            validateSyncIntegrity(totalItems, totalCreated)
            database.databaseQueries.setFullSyncInProgress(0)
            database.databaseQueries.updateDeltaSyncEtag(null)

            logger.info { "Full sync complete. Total items: $totalCreated, errors: $totalErrors" }
        } finally {
            fullSyncReceivedIds = null
        }
    }

    private suspend fun performDeltaSync(
        initialSessionId: String,
        lastSyncCursor: Long,
        initialSessionExpiresAt: Instant?,
    ) {
        updateProgress(phase = SyncPhase.FETCHING_DELTA)

        // ETag-based 304 short-circuiting is no longer wired in this path: the
        // generated [SyncApi.deltaSyncV1SyncDeltaGet] does not surface the
        // `If-None-Match` header today, and the global [HttpRequestRetry]
        // already handles transient failures. The stored ETag column is
        // preserved for backward compatibility with other call sites.

        var sessionId = initialSessionId
        var sessionExpiresAt = initialSessionExpiresAt
        var hasMore = true
        var cursor = lastSyncCursor
        var totalCreated = 0
        var totalUpdated = 0
        var totalDeleted = 0
        var batchNumber = 0

        while (hasMore) {
            coroutineContext.ensureActive()
            val renewed = sessionCoordinator.renew(sessionId, sessionExpiresAt)
            sessionId = renewed.first
            sessionExpiresAt = renewed.second

            val previousCursor = cursor
            val result = deltaSync(sessionId, since = cursor, limit = DEFAULT_BATCH_SIZE, etag = null)
            totalCreated += result.createdCount
            totalUpdated += result.updatedCount
            totalDeleted += result.deletedCount
            hasMore = result.hasMore
            batchNumber++

            val processedItems = totalCreated + totalUpdated + totalDeleted
            updateProgress(
                phase = SyncPhase.FETCHING_DELTA,
                processedItems = processedItems,
                currentBatch = batchNumber,
            )

            val nextCursor = result.nextCursor
            if (nextCursor != null) {
                cursor = nextCursor
            }

            // Guard: break if cursor did not advance (prevents infinite loop)
            if (hasMore && cursor == previousCursor) {
                throw IllegalStateException("Delta sync cursor did not advance (stuck at $cursor)")
            }

            // Note: Delta sync saves checkpoint in deltaSync() method itself
        }

        logger.info {
            "Delta sync complete. Created: $totalCreated, Updated: $totalUpdated, Deleted: $totalDeleted"
        }
    }

    private fun cleanupStaleSummaries(receivedIds: Set<String>) {
        database.transaction {
            val localIds = database.databaseQueries.getAllSummaryIds().executeAsList()
            val staleIds = localIds.filterNot { it in receivedIds }
            if (staleIds.isNotEmpty()) {
                logger.info { "Removing ${staleIds.size} stale summaries not present on server" }
                staleIds.forEach { id ->
                    database.databaseQueries.deleteSummary(id)
                }
            }
        }
    }

    private fun validateSyncIntegrity(
        expectedCount: Int?,
        actualCount: Int,
    ) {
        if (expectedCount != null && expectedCount != actualCount) {
            logger.warn {
                "Sync integrity mismatch: expected $expectedCount items, got $actualCount"
            }
            // Update progress with warning but don't fail
            updateProgress(
                phase = SyncPhase.VALIDATING,
                errorMessage = "Integrity warning: expected $expectedCount items, synced $actualCount",
            )
        } else {
            updateProgress(phase = SyncPhase.VALIDATING)
            logger.info { "Sync integrity validated: $actualCount items" }
        }
    }

    override fun getSyncState(): Flow<SyncState> {
        return database.databaseQueries.getSyncMetadata()
            .asFlow().mapToOneOrNull(ioDispatcher).map { entity ->
                SyncState(
                    lastSyncTime = entity?.lastSyncTime,
                    lastSyncHash = entity?.syncToken,
                )
            }
    }

    // ========================================================================
    // Session-Based Sync (new OpenAPI spec)
    // ========================================================================

    override suspend fun createSyncSession(limit: Int?): String = sessionCoordinator.createWithLimit(limit)

    override suspend fun fullSync(
        sessionId: String,
        limit: Int?,
        cursor: Long?,
    ): SyncResult {
        logger.info { "Starting full sync with sessionId=$sessionId, limit=$limit, cursor=$cursor" }
        // Note: the generated [SyncApi.fullSyncV1SyncFullGet] does not expose
        // a `cursor` parameter (the OpenAPI spec only has `session_id` and
        // `limit`). The session itself tracks the cursor server-side via
        // `lastIssuedSince`. The `cursor` argument is preserved on the
        // domain interface for source-compatibility but unused here.
        val envelope =
            SyncApi
                .fullSyncV1SyncFullGet(
                    sessionId = sessionId,
                    limit = limit?.toLong(),
                ).unwrap()

        val data =
            envelope.data
                ?: throw IllegalStateException("Full sync response missing data")
        val items = data.items
        logger.info { "Full sync received ${items.size} items, hasMore=${data.hasMore}" }

        var successCount = 0
        var errorCount = 0

        // Process items in sub-chunks for partial-failure resilience
        items.chunked(TRANSACTION_CHUNK_SIZE).forEach { chunk ->
            database.transaction {
                chunk.forEach { item: SyncEntityEnvelope ->
                    val bridged = item.toSyncItemDto()
                    if (syncItemApplierRegistry.apply(bridged)) {
                        successCount++
                        fullSyncReceivedIds?.add(bridged.idAsString)
                    } else {
                        errorCount++
                    }
                }
            }
        }

        // Checkpoint outside item transactions
        val currentMetadata = database.databaseQueries.getSyncMetadata().executeAsOneOrNull()
        val currentFullSyncInProgress = currentMetadata?.fullSyncInProgress ?: 0L
        database.transaction {
            database.databaseQueries.updateSyncMetadata(
                lastSyncTime = Clock.System.now(),
                syncToken = syncCheckpointToken(data.nextSince, serverVersion = null, currentMetadata?.syncToken),
                fullSyncInProgress = currentFullSyncInProgress,
            )
        }

        // Progress update happens outside the transaction. This is intentional: StateFlow
        // updates are thread-safe and we avoid holding the DB transaction open longer than
        // needed. A crash between transaction commit and this update is acceptable because
        // the DB state is the source of truth for sync position.
        if (errorCount > 0) {
            _syncProgress.update { current ->
                current?.copy(errorCount = (current.errorCount) + errorCount)
            }
            logger.warn { "Full sync batch: $successCount success, $errorCount errors" }
        }

        return SyncResult(
            createdCount = successCount,
            updatedCount = 0,
            deletedCount = 0,
            hasMore = data.hasMore,
            nextCursor = data.nextSince,
            serverVersion = null,
        )
    }

    override suspend fun deltaSync(
        sessionId: String,
        since: Long,
        limit: Int?,
        etag: String?,
    ): SyncResult {
        logger.info { "Starting delta sync with sessionId=$sessionId, since=$since, limit=$limit" }
        // Note: the generated [SyncApi.deltaSyncV1SyncDeltaGet] does not yet
        // surface the `If-None-Match` header. The `etag` parameter is kept
        // on the domain interface for source-compatibility; until the spec
        // exposes it, 304 short-circuiting is bypassed and we always parse
        // the response body.
        val envelope =
            SyncApi
                .deltaSyncV1SyncDeltaGet(
                    sessionId = sessionId,
                    since = since,
                    limit = limit?.toLong(),
                ).unwrap()

        val data =
            envelope.data
                ?: throw IllegalStateException("Delta sync response missing data")
        logger.info {
            "Delta sync received: created=${data.created.size}, " +
                "updated=${data.updated.size}, deleted=${data.deleted.size}, hasMore=${data.hasMore}"
        }

        var createdCount = 0
        var updatedCount = 0
        var errorCount = 0

        data.created.chunked(TRANSACTION_CHUNK_SIZE).forEach { chunk ->
            database.transaction {
                chunk.forEach { item ->
                    if (syncItemApplierRegistry.apply(item.toSyncItemDto())) createdCount++ else errorCount++
                }
            }
        }

        data.updated.chunked(TRANSACTION_CHUNK_SIZE).forEach { chunk ->
            database.transaction {
                chunk.forEach { item ->
                    if (syncItemApplierRegistry.apply(item.toSyncItemDto())) updatedCount++ else errorCount++
                }
            }
        }

        data.deleted.chunked(TRANSACTION_CHUNK_SIZE).forEach { chunk ->
            database.transaction {
                chunk.forEach { item ->
                    val bridged = item.toSyncItemDto()
                    when (bridged.entityType) {
                        "summary" -> database.databaseQueries.deleteSummary(bridged.idAsString)
                        "highlight" -> database.databaseQueries.deleteHighlightById(bridged.idAsString)
                        "tag" -> bridged.idAsLong?.let { database.databaseQueries.deleteTag(it) }
                        "summary_tag" -> {
                            // Summary-tag associations are cleaned up via tag delete cascade
                        }
                        else -> logger.debug { "Ignoring delete for entity type: ${bridged.entityType}" }
                    }
                }
            }
        }

        database.transaction {
            database.databaseQueries.updateSyncMetadata(
                lastSyncTime = Clock.System.now(),
                syncToken = syncCheckpointToken(data.nextSince, serverVersion = null, since.toString()),
                fullSyncInProgress = 0,
            )
        }

        // Progress update happens outside the transaction (see fullSync comment for rationale)
        if (errorCount > 0) {
            _syncProgress.update { current ->
                current?.copy(errorCount = (current.errorCount) + errorCount)
            }
            logger.warn { "Delta sync batch: created=$createdCount, updated=$updatedCount, errors=$errorCount" }
        }

        return SyncResult(
            createdCount = createdCount,
            updatedCount = updatedCount,
            deletedCount = data.deleted.size,
            hasMore = data.hasMore,
            nextCursor = data.nextSince,
            serverVersion = null,
        )
    }

    override suspend fun applyChanges(
        sessionId: String,
        changes: List<LocalChange>,
    ): ApplyResult {
        if (changes.isEmpty()) {
            return ApplyResult(appliedCount = 0, conflicts = emptyList(), serverVersion = null)
        }

        logger.info { "Applying ${changes.size} local changes to server" }

        val request = buildSyncApplyRequest(sessionId = sessionId, changes = changes)

        val envelope =
            SyncApi
                .applyChangesV1SyncApplyPost(body = request)
                .unwrap()
        val data =
            envelope.data
                ?: throw IllegalStateException("Apply changes response missing data")

        val results = data.results
        val appliedResults = results.filter { it.status == SyncApplyResult.Status.APPLIED }
        val conflictResults = data.conflicts ?: results.filter { it.status == SyncApplyResult.Status.CONFLICT }
        logger.info { "Applied ${appliedResults.size} changes, ${conflictResults.size} conflicts" }

        return ApplyResult(
            appliedCount = appliedResults.size,
            conflicts =
                conflictResults.map { conflict ->
                    SyncConflict(
                        id = conflict.idAsLong() ?: 0L,
                        entityType = conflict.entityType,
                        clientVersion = 0L,
                        serverVersion = conflict.serverVersion ?: 0L,
                        reason = conflict.errorCode ?: conflict.message ?: "conflict",
                    )
                },
            serverVersion = appliedResults.firstOrNull()?.serverVersion,
        )
    }
}
