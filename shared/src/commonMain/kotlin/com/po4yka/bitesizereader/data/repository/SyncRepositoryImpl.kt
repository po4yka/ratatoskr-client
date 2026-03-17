package com.po4yka.bitesizereader.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.po4yka.bitesizereader.data.mappers.toDto
import com.po4yka.bitesizereader.data.mappers.toSummaryEntity
import com.po4yka.bitesizereader.data.remote.SyncApi
import com.po4yka.bitesizereader.data.remote.dto.SyncApplyRequestDto
import com.po4yka.bitesizereader.data.remote.dto.SyncItemDto
import com.po4yka.bitesizereader.data.remote.dto.SyncSessionRequestDto
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.domain.model.SyncConflict
import com.po4yka.bitesizereader.domain.model.SyncPhase
import com.po4yka.bitesizereader.domain.model.SyncProgress
import com.po4yka.bitesizereader.domain.model.SyncResult
import com.po4yka.bitesizereader.domain.model.SyncState
import com.po4yka.bitesizereader.domain.repository.ApplyResult
import com.po4yka.bitesizereader.domain.repository.LocalChange
import com.po4yka.bitesizereader.domain.repository.SyncRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.longOrNull
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
import kotlin.coroutines.coroutineContext
import kotlin.time.Clock
import kotlin.time.Instant
import com.po4yka.bitesizereader.util.network.NetworkMonitor
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

/** Default batch size for sync operations */
private const val DEFAULT_BATCH_SIZE = 100

/** Sub-chunk size for database transactions within a batch */
private const val TRANSACTION_CHUNK_SIZE = 25

/** Timeout for the entire sync operation (5 minutes) */
private const val SYNC_TIMEOUT_MS = 5 * 60 * 1000L

@Single(binds = [SyncRepository::class])
class SyncRepositoryImpl(
    private val database: Database,
    private val api: SyncApi,
    private val networkMonitor: NetworkMonitor,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : SyncRepository {
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

    private data class SyncSessionInfo(
        val sessionId: String,
        val totalItems: Int?,
        val expiresAt: Instant?,
    )

    private suspend fun createSyncSessionInternal(): SyncSessionInfo {
        val request: SyncSessionRequestDto? = null
        val response = api.createSession(request)
        if (response.success && response.data != null) {
            val data = response.data
            logger.info { "Created sync session: ${data.sessionId}, totalItems=${data.totalItems}" }
            val expiresAt =
                data.expiresAt?.let {
                    try {
                        Instant.parse(it)
                    } catch (_: Exception) {
                        null
                    }
                }
            return SyncSessionInfo(
                sessionId = data.sessionId,
                totalItems = data.totalItems,
                expiresAt = expiresAt,
            )
        } else {
            throw IllegalStateException(response.error?.message ?: "Failed to create sync session")
        }
    }

    private suspend fun syncWithSessionBasedEndpoint(forceFull: Boolean) {
        logger.info { "Starting session-based sync, forceFull=$forceFull" }
        _syncProgress.update {
            SyncProgress(
                phase = SyncPhase.CREATING_SESSION,
                startTime = Clock.System.now(),
            )
        }

        coroutineContext.ensureActive()
        val session = createSyncSessionInternal()

        // Flush pending operations before sync
        flushPendingOperations(session.sessionId)

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

        // Mark full sync as in progress
        database.databaseQueries.setFullSyncInProgress(1)

        val receivedIds = mutableSetOf<String>()
        fullSyncReceivedIds = receivedIds

        var sessionId = initialSessionId
        var sessionExpiresAt = initialSessionExpiresAt
        var hasMore = true
        var totalCreated = 0
        var totalErrors = 0
        var cursor: Long? = resumeCursor
        var previousCursor: Long? = null
        var batchNumber = 0
        val totalBatches = totalItems?.let { (it + DEFAULT_BATCH_SIZE - 1) / DEFAULT_BATCH_SIZE }

        try {
            // First batch
            coroutineContext.ensureActive()
            val renewed = createOrRenewSession(sessionId, sessionExpiresAt)
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
                logger.warn { "Server reports hasMore=true but returned null cursor, stopping" }
                hasMore = false
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
                val renewedInLoop = createOrRenewSession(sessionId, sessionExpiresAt)
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
                    logger.warn {
                        "Cursor did not advance (was=$previousCursor, now=$cursor), breaking sync loop"
                    }
                    break
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

            // Remove stale summaries not present on server
            cleanupStaleSummaries(receivedIds)

            // Validate integrity
            validateSyncIntegrity(totalItems, totalCreated)

            // Mark full sync as complete and clear stale ETag
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

        val storedEtag = database.databaseQueries.getDeltaSyncEtag().executeAsOneOrNull()?.deltaSyncEtag

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
            val renewed = createOrRenewSession(sessionId, sessionExpiresAt)
            sessionId = renewed.first
            sessionExpiresAt = renewed.second

            val previousCursor = cursor
            val currentEtag = if (cursor == lastSyncCursor) storedEtag else null
            val result = deltaSync(sessionId, since = cursor, limit = DEFAULT_BATCH_SIZE, etag = currentEtag)
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

            if (result.nextCursor != null) {
                cursor = result.nextCursor
            }

            // Guard: break if cursor did not advance (prevents infinite loop)
            if (hasMore && cursor == previousCursor) {
                logger.warn {
                    "Delta sync cursor did not advance (stuck at $cursor), breaking sync loop"
                }
                break
            }

            // Note: Delta sync saves checkpoint in deltaSync() method itself
        }

        logger.info {
            "Delta sync complete. Created: $totalCreated, Updated: $totalUpdated, Deleted: $totalDeleted"
        }
    }

    /**
     * Returns the current session if still valid, or creates a new one if expired.
     */
    private suspend fun createOrRenewSession(
        currentSessionId: String,
        sessionExpiresAt: Instant?,
    ): Pair<String, Instant?> {
        if (sessionExpiresAt == null || Clock.System.now() < sessionExpiresAt) {
            return currentSessionId to sessionExpiresAt
        }
        logger.info { "Session expired, creating new session" }
        val newSession = createSyncSessionInternal()
        return newSession.sessionId to newSession.expiresAt
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

    override suspend fun createSyncSession(limit: Int?): String {
        val request = limit?.let { SyncSessionRequestDto(limit = it) }
        val response = api.createSession(request)
        if (response.success && response.data != null) {
            logger.info { "Created sync session: ${response.data.sessionId}" }
            return response.data.sessionId
        } else {
            throw IllegalStateException(response.error?.message ?: "Failed to create sync session")
        }
    }

    override suspend fun fullSync(
        sessionId: String,
        limit: Int?,
        cursor: Long?,
    ): SyncResult {
        logger.info { "Starting full sync with sessionId=$sessionId, limit=$limit, cursor=$cursor" }
        val response = api.fullSync(sessionId, limit, cursor)

        if (!response.success || response.data == null) {
            logger.error { "Full sync failed: ${response.error}" }
            throw IllegalStateException(response.error?.message ?: "Full sync failed")
        }

        val data = response.data
        logger.info { "Full sync received ${data.items.size} items, hasMore=${data.hasMore}" }

        // Track processing results
        var successCount = 0
        var errorCount = 0

        // Process items in sub-chunks for partial-failure resilience
        data.items.chunked(TRANSACTION_CHUNK_SIZE).forEach { chunk ->
            database.transaction {
                chunk.forEach { item ->
                    if (processSyncItem(item)) {
                        successCount++
                        fullSyncReceivedIds?.add(item.id.toString())
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
                syncToken = data.nextCursor?.toString(),
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
            nextCursor = data.nextCursor,
            serverVersion = data.serverVersion,
        )
    }

    override suspend fun deltaSync(
        sessionId: String,
        since: Long,
        limit: Int?,
        etag: String?,
    ): SyncResult {
        logger.info { "Starting delta sync with sessionId=$sessionId, since=$since, limit=$limit" }
        val deltaSyncResult = api.deltaSync(sessionId, since, limit, etag)

        // Handle 304 Not Modified
        if (deltaSyncResult.response == null) {
            logger.info { "Delta sync returned 304 Not Modified, no changes" }
            deltaSyncResult.etag?.let { newEtag ->
                database.databaseQueries.updateDeltaSyncEtag(newEtag)
            }
            return SyncResult(
                createdCount = 0,
                updatedCount = 0,
                deletedCount = 0,
                hasMore = false,
                nextCursor = since,
                serverVersion = null,
            )
        }

        val response = deltaSyncResult.response

        if (!response.success || response.data == null) {
            logger.error { "Delta sync failed: ${response.error}" }
            throw IllegalStateException(response.error?.message ?: "Delta sync failed")
        }

        val data = response.data
        logger.info {
            "Delta sync received: created=${data.created.size}, " +
                "updated=${data.updated.size}, deleted=${data.deleted.size}, hasMore=${data.hasMore}"
        }

        // Track processing results
        var createdCount = 0
        var updatedCount = 0
        var errorCount = 0

        // Process created items in sub-chunks
        data.created.chunked(TRANSACTION_CHUNK_SIZE).forEach { chunk ->
            database.transaction {
                chunk.forEach { item ->
                    if (processSyncItem(item)) createdCount++ else errorCount++
                }
            }
        }

        // Process updated items in sub-chunks
        data.updated.chunked(TRANSACTION_CHUNK_SIZE).forEach { chunk ->
            database.transaction {
                chunk.forEach { item ->
                    if (processSyncItem(item)) updatedCount++ else errorCount++
                }
            }
        }

        // Process deletes in sub-chunks
        data.deleted.chunked(TRANSACTION_CHUNK_SIZE).forEach { chunk ->
            database.transaction {
                chunk.forEach { id ->
                    database.databaseQueries.deleteSummary(id.toString())
                }
            }
        }

        // Update sync metadata (checkpoint)
        database.transaction {
            database.databaseQueries.updateSyncMetadata(
                lastSyncTime = Clock.System.now(),
                syncToken = data.newCursor?.toString(),
                fullSyncInProgress = 0,
            )
        }

        // Save new ETag for future conditional requests
        deltaSyncResult.etag?.let { newEtag ->
            database.databaseQueries.updateDeltaSyncEtag(newEtag)
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
            nextCursor = data.newCursor,
            serverVersion = data.serverVersion,
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

        val request =
            SyncApplyRequestDto(
                sessionId = sessionId,
                changes = changes.map { it.toDto() },
            )

        val response = api.applyChanges(request)
        if (!response.success || response.data == null) {
            logger.error { "Apply changes failed: ${response.error}" }
            throw IllegalStateException(response.error?.message ?: "Apply changes failed")
        }

        val data = response.data
        logger.info { "Applied ${data.applied.size} changes, ${data.conflicts.size} conflicts" }

        return ApplyResult(
            appliedCount = data.applied.size,
            conflicts =
                data.conflicts.map { conflict ->
                    SyncConflict(
                        id = conflict.id,
                        entityType = conflict.entityType,
                        clientVersion = conflict.clientVersion,
                        serverVersion = conflict.serverVersion,
                        reason = conflict.reason,
                    )
                },
            serverVersion = data.serverVersion,
        )
    }

    /**
     * Process a single sync item by dispatching on its entity type.
     * Must be called within a database transaction.
     *
     * @return true if item was processed successfully, false if there was an error
     */
    private fun processSyncItem(item: SyncItemDto): Boolean {
        return when (item.entityType) {
            "summary" -> {
                val entity = item.summary?.toSummaryEntity(item.id)
                if (entity != null) {
                    database.databaseQueries.insertSummary(entity)
                    true
                } else {
                    false
                }
            }
            "highlight" -> {
                // TODO: Sync highlights with backend when API is available
                true
            }
            else -> {
                logger.warn { "Unknown entity type: ${item.entityType}" }
                false
            }
        }
    }

    private suspend fun flushPendingOperations(sessionId: String) {
        val pendingOps = database.databaseQueries.selectAllPendingOperations().executeAsList()
        if (pendingOps.isEmpty()) return

        logger.info { "Flushing ${pendingOps.size} pending operations" }

        val changes =
            pendingOps.mapNotNull { op ->
                // Highlight operations cannot be synced yet (no backend API), skip them
                if (op.entityType == "highlight") {
                    logger.debug { "Skipping highlight pending operation: ${op.action} for ${op.entityId}" }
                    return@mapNotNull null
                }
                val remoteId = op.entityId.toLongOrNull() ?: return@mapNotNull null
                when (op.action) {
                    "delete" ->
                        LocalChange(
                            entityType = op.entityType,
                            id = remoteId,
                            action = "delete",
                            lastSeenVersion = 0,
                            payload = null,
                            clientTimestamp = null,
                        )
                    "update_read" -> {
                        val payloadMap = op.payload?.let { parsePayload(it) }
                        LocalChange(
                            entityType = op.entityType,
                            id = remoteId,
                            action = "update",
                            lastSeenVersion = 0,
                            payload = payloadMap,
                            clientTimestamp = null,
                        )
                    }
                    "toggle_favorite" ->
                        LocalChange(
                            entityType = op.entityType,
                            id = remoteId,
                            action = "update",
                            lastSeenVersion = 0,
                            payload = mapOf("toggle_favorite" to true),
                            clientTimestamp = null,
                        )
                    else -> {
                        logger.warn { "Unknown pending operation action: ${op.action}" }
                        null
                    }
                }
            }

        if (changes.isEmpty()) {
            database.databaseQueries.deleteAllPendingOperations()
            return
        }

        try {
            val result = applyChanges(sessionId, changes)
            database.databaseQueries.deleteAllPendingOperations()
            // Also clear legacy pending deletes table
            database.databaseQueries.deleteAllPendingDeletes()
            if (result.conflicts.isNotEmpty()) {
                // Server wins: conflicting local changes are discarded.
                // The server version will overwrite local data on the next delta sync.
                logger.warn {
                    "${result.conflicts.size} pending operation(s) rejected by server (server wins):"
                }
                result.conflicts.forEach { conflict ->
                    logger.warn {
                        "  conflict: entity=${conflict.entityType}#${conflict.id} " +
                            "reason=${conflict.reason} " +
                            "clientVersion=${conflict.clientVersion} serverVersion=${conflict.serverVersion}"
                    }
                }
                _syncProgress.update { current ->
                    current?.copy(errorCount = (current.errorCount) + result.conflicts.size)
                }
            }
            logger.info {
                "Successfully flushed ${changes.size} pending operations " +
                    "(applied=${result.appliedCount}, conflicts=${result.conflicts.size})"
            }
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.warn(e) { "Failed to flush pending operations, will retry next sync" }
        }
    }

    private fun parsePayload(json: String): Map<String, Any?> {
        return try {
            val jsonObject =
                kotlinx.serialization.json.Json.parseToJsonElement(json)
                    .jsonObject
            jsonObject.mapValues { (_, value) ->
                when {
                    value is JsonPrimitive && value.isString -> value.content
                    value is JsonPrimitive -> {
                        value.booleanOrNull ?: value.longOrNull ?: value.doubleOrNull ?: value.content
                    }
                    else -> value.toString()
                }
            }
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.warn(e) { "Failed to parse pending operation payload: $json" }
            emptyMap()
        }
    }
}
