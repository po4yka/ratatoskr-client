package com.po4yka.bitesizereader.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.po4yka.bitesizereader.data.remote.SyncApi
import com.po4yka.bitesizereader.data.remote.dto.SyncApplyItemDto
import com.po4yka.bitesizereader.data.remote.dto.SyncApplyRequestDto
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.coroutines.coroutineContext
import kotlin.time.Clock
import kotlin.time.Instant
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

/** Default batch size for sync operations */
private const val DEFAULT_BATCH_SIZE = 100

/** Timeout for the entire sync operation (5 minutes) */
private const val SYNC_TIMEOUT_MS = 5 * 60 * 1000L

@Single(binds = [SyncRepository::class])
class SyncRepositoryImpl(
    private val database: Database,
    private val api: SyncApi,
) : SyncRepository {
    // ========================================================================
    // Progress Tracking
    // ========================================================================

    private val _syncProgress = MutableStateFlow<SyncProgress?>(null)
    override val syncProgress: StateFlow<SyncProgress?> = _syncProgress.asStateFlow()

    private var currentSyncJob: Job? = null
    private val syncMutex = Mutex()
    private var fullSyncReceivedIds: MutableSet<String>? = null

    override fun cancelSync() {
        currentSyncJob?.cancel()
        _syncProgress.value?.let { progress ->
            _syncProgress.value = progress.copy(phase = SyncPhase.CANCELLED)
        }
        logger.info { "Sync cancelled by user" }
    }

    private fun updateProgress(
        phase: SyncPhase,
        totalItems: Int? = _syncProgress.value?.totalItems,
        processedItems: Int = _syncProgress.value?.processedItems ?: 0,
        currentBatch: Int = _syncProgress.value?.currentBatch ?: 0,
        totalBatches: Int? = _syncProgress.value?.totalBatches,
        errorCount: Int = _syncProgress.value?.errorCount ?: 0,
        errorMessage: String? = null,
    ) {
        val startTime = _syncProgress.value?.startTime ?: Clock.System.now()
        _syncProgress.value =
            SyncProgress(
                phase = phase,
                totalItems = totalItems,
                processedItems = processedItems,
                currentBatch = currentBatch,
                totalBatches = totalBatches,
                errorCount = errorCount,
                startTime = startTime,
                errorMessage = errorMessage,
            )
    }

    // ========================================================================
    // Main Sync Entry Point
    // ========================================================================

    override suspend fun sync(forceFull: Boolean) {
        if (!syncMutex.tryLock()) {
            logger.info { "Sync already in progress, skipping" }
            return
        }
        currentSyncJob = coroutineContext[Job]
        try {
            withTimeout(SYNC_TIMEOUT_MS) {
                syncWithSessionBasedEndpoint(forceFull)
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
        _syncProgress.value =
            SyncProgress(
                phase = SyncPhase.CREATING_SESSION,
                startTime = Clock.System.now(),
            )

        coroutineContext.ensureActive()
        val session = createSyncSessionInternal()

        // Flush pending deletes before sync
        flushPendingDeletes(session.sessionId)

        val metadata = database.databaseQueries.getSyncMetadata().executeAsOneOrNull()
        val lastSyncCursor = metadata?.syncToken?.toLongOrNull() ?: 0L

        if (forceFull || lastSyncCursor == 0L) {
            performFullSync(session.sessionId, session.totalItems, session.expiresAt)
        } else {
            performDeltaSync(session.sessionId, lastSyncCursor, session.expiresAt)
        }

        updateProgress(phase = SyncPhase.COMPLETED)
        logger.info { "Sync completed successfully" }
    }

    private suspend fun performFullSync(
        sessionId: String,
        totalItems: Int?,
        sessionExpiresAt: Instant?,
    ) {
        updateProgress(phase = SyncPhase.FETCHING_FULL)

        val receivedIds = mutableSetOf<String>()
        fullSyncReceivedIds = receivedIds

        var hasMore = true
        var totalCreated = 0
        var totalErrors = 0
        var cursor: Long? = null
        var batchNumber = 0
        val totalBatches = totalItems?.let { (it + DEFAULT_BATCH_SIZE - 1) / DEFAULT_BATCH_SIZE }

        try {
            // First batch
            coroutineContext.ensureActive()
            checkSessionExpiry(sessionExpiresAt)
            val firstResult = fullSync(sessionId, limit = DEFAULT_BATCH_SIZE, cursor = cursor)

            totalCreated += firstResult.createdCount
            hasMore = firstResult.hasMore
            cursor = firstResult.nextCursor
            batchNumber++

            updateProgress(
                phase = SyncPhase.FETCHING_FULL,
                totalItems = totalItems,
                processedItems = totalCreated,
                currentBatch = batchNumber,
                totalBatches = totalBatches,
            )

            while (hasMore) {
                coroutineContext.ensureActive()
                checkSessionExpiry(sessionExpiresAt)

                val result = fullSync(sessionId, limit = DEFAULT_BATCH_SIZE, cursor = cursor)
                totalCreated += result.createdCount
                hasMore = result.hasMore
                cursor = result.nextCursor
                batchNumber++

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

            logger.info { "Full sync complete. Total items: $totalCreated, errors: $totalErrors" }
        } finally {
            fullSyncReceivedIds = null
        }
    }

    private suspend fun performDeltaSync(
        sessionId: String,
        lastSyncCursor: Long,
        sessionExpiresAt: Instant?,
    ) {
        updateProgress(phase = SyncPhase.FETCHING_DELTA)

        var hasMore = true
        var cursor = lastSyncCursor
        var totalCreated = 0
        var totalUpdated = 0
        var totalDeleted = 0
        var batchNumber = 0

        while (hasMore) {
            coroutineContext.ensureActive()
            checkSessionExpiry(sessionExpiresAt)

            val result = deltaSync(sessionId, since = cursor, limit = DEFAULT_BATCH_SIZE)
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

            // Note: Delta sync saves checkpoint in deltaSync() method itself
        }

        logger.info {
            "Delta sync complete. Created: $totalCreated, Updated: $totalUpdated, Deleted: $totalDeleted"
        }
    }

    private fun checkSessionExpiry(sessionExpiresAt: Instant?) {
        if (sessionExpiresAt != null && Clock.System.now() >= sessionExpiresAt) {
            throw IllegalStateException("Sync session expired")
        }
    }

    private fun cleanupStaleSummaries(receivedIds: Set<String>) {
        val localIds = database.databaseQueries.getAllSummaryIds().executeAsList().toSet()
        val staleIds = localIds - receivedIds
        if (staleIds.isNotEmpty()) {
            logger.info { "Removing ${staleIds.size} stale summaries not present on server" }
            database.transaction {
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
            .asFlow().mapToOneOrNull(Dispatchers.IO).map { entity ->
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

        // Process items and store in database
        database.transaction {
            data.items.forEach { item ->
                when (item.entityType) {
                    "summary" -> {
                        val success = processSyncSummaryItem(item.id, item.summary)
                        if (success) {
                            successCount++
                            fullSyncReceivedIds?.add(item.id.toString())
                        } else {
                            errorCount++
                        }
                    }
                    else -> {
                        logger.warn { "Unknown entity type: ${item.entityType}" }
                        errorCount++
                    }
                }
            }
            // Checkpoint inside transaction
            database.databaseQueries.updateSyncMetadata(
                lastSyncTime = Clock.System.now(),
                syncToken = data.nextCursor?.toString() ?: "",
            )
        }

        // Update progress with error count
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
    ): SyncResult {
        logger.info { "Starting delta sync with sessionId=$sessionId, since=$since, limit=$limit" }
        val response = api.deltaSync(sessionId, since, limit)

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

        // Process changes and store in database
        database.transaction {
            data.created.forEach { item ->
                when (item.entityType) {
                    "summary" -> {
                        val success = processSyncSummaryItem(item.id, item.summary)
                        if (success) createdCount++ else errorCount++
                    }
                    else -> {
                        logger.warn { "Unknown entity type: ${item.entityType}" }
                        errorCount++
                    }
                }
            }
            data.updated.forEach { item ->
                when (item.entityType) {
                    "summary" -> {
                        val success = processSyncSummaryItem(item.id, item.summary)
                        if (success) updatedCount++ else errorCount++
                    }
                    else -> {
                        logger.warn { "Unknown entity type: ${item.entityType}" }
                        errorCount++
                    }
                }
            }
            data.deleted.forEach { id ->
                database.databaseQueries.deleteSummary(id.toString())
            }

            // Update sync metadata (checkpoint)
            database.databaseQueries.updateSyncMetadata(
                lastSyncTime = Clock.System.now(),
                syncToken = data.newCursor?.toString() ?: "",
            )
        }

        // Update progress with error count
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

    // ========================================================================
    // Private Helpers
    // ========================================================================

    /**
     * Process a sync summary item and persist to database.
     *
     * Server sends summary data in this structure:
     * {
     *   "id": 42,
     *   "request_id": 10,
     *   "lang": "en",
     *   "is_read": false,
     *   "json_payload": {
     *     "summary_1000": "Full summary text...",
     *     "topic_tags": ["#tag1", "#tag2"],
     *     "metadata": {
     *       "title": "Article Title",
     *       "canonical_url": "https://...",
     *       "domain": "example.com"
     *     }
     *   },
     *   "created_at": "2024-12-14T10:20:00Z"
     * }
     *
     * @return true if item was processed successfully, false if there was an error
     */
    private fun processSyncSummaryItem(
        id: Long,
        summaryData: JsonObject?,
    ): Boolean {
        if (summaryData == null) {
            logger.warn { "Sync item $id has no summary data, skipping" }
            return false
        }

        return try {
            // Extract top-level fields
            val isRead = summaryData["is_read"]?.jsonPrimitive?.booleanOrNull ?: false
            val createdAtStr = summaryData["created_at"]?.jsonPrimitive?.contentOrNull
            val jsonPayload = summaryData["json_payload"]?.jsonObject

            // Parse created_at timestamp
            val createdAt =
                createdAtStr?.let {
                    try {
                        Instant.parse(it)
                    } catch (_: Exception) {
                        logger.warn { "Failed to parse created_at for item $id: $it" }
                        Clock.System.now()
                    }
                } ?: run {
                    logger.warn { "Missing created_at for item $id, using current time" }
                    Clock.System.now()
                }

            // Extract from json_payload
            val metadata = jsonPayload?.get("metadata")?.jsonObject
            val title =
                metadata?.get("title")?.jsonPrimitive?.contentOrNull
                    ?: "Untitled Summary"
            val sourceUrl =
                metadata?.get("canonical_url")?.jsonPrimitive?.contentOrNull
                    ?: ""
            val content =
                jsonPayload?.get("summary_1000")?.jsonPrimitive?.contentOrNull
                    ?: jsonPayload?.get("summary_250")?.jsonPrimitive?.contentOrNull
                    ?: ""

            // Extract tags from topic_tags (they come as ["#tag1", "#tag2"])
            val topicTags = jsonPayload?.get("topic_tags")?.jsonArray
            val tags =
                topicTags?.mapNotNull { tag ->
                    tag.jsonPrimitive.contentOrNull?.removePrefix("#")
                } ?: emptyList()

            // Note: imageUrl is not provided by the server in the current schema
            val imageUrl: String? = null

            logger.debug {
                "Inserting summary $id: title='$title', sourceUrl='$sourceUrl', " +
                    "contentLength=${content.length}, tags=$tags, isRead=$isRead"
            }

            // Insert or replace in database
            database.databaseQueries.insertSummary(
                com.po4yka.bitesizereader.database.SummaryEntity(
                    id = id.toString(),
                    title = title,
                    content = content,
                    sourceUrl = sourceUrl,
                    imageUrl = imageUrl,
                    createdAt = createdAt,
                    isRead = isRead,
                    tags = tags,
                    readingTimeMin = null,
                ),
            )
            true
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.error(e) { "Failed to process sync summary item $id" }
            false
        }
    }

    private suspend fun flushPendingDeletes(sessionId: String) {
        val pendingDeletes = database.databaseQueries.selectAllPendingDeletes().executeAsList()
        if (pendingDeletes.isEmpty()) return

        logger.info { "Flushing ${pendingDeletes.size} pending deletes" }

        val changes =
            pendingDeletes.mapNotNull { pending ->
                val remoteId = pending.id.toLongOrNull() ?: return@mapNotNull null
                LocalChange(
                    entityType = "summary",
                    id = remoteId,
                    action = "delete",
                    lastSeenVersion = 0,
                    payload = null,
                    clientTimestamp = null,
                )
            }

        if (changes.isEmpty()) {
            // Clean up any non-numeric IDs
            database.databaseQueries.deleteAllPendingDeletes()
            return
        }

        try {
            applyChanges(sessionId, changes)
            database.databaseQueries.deleteAllPendingDeletes()
            logger.info { "Successfully flushed ${changes.size} pending deletes" }
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.warn(e) { "Failed to flush pending deletes, will retry next sync" }
        }
    }

    private fun LocalChange.toDto(): SyncApplyItemDto {
        val jsonPayload =
            payload?.let { map ->
                JsonObject(
                    map.mapValues { (_, value) ->
                        when (value) {
                            is String -> JsonPrimitive(value)
                            is Number -> JsonPrimitive(value)
                            is Boolean -> JsonPrimitive(value)
                            else -> JsonPrimitive(value?.toString())
                        }
                    },
                )
            }

        return SyncApplyItemDto(
            entityType = entityType,
            id = id,
            action = action,
            lastSeenVersion = lastSeenVersion,
            payload = jsonPayload,
            clientTimestamp = clientTimestamp,
        )
    }
}
