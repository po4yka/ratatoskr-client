package com.po4yka.bitesizereader.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.po4yka.bitesizereader.data.local.SecureStorage
import com.po4yka.bitesizereader.data.mappers.toEntity
import com.po4yka.bitesizereader.data.remote.SyncApi
import com.po4yka.bitesizereader.data.remote.dto.SyncApplyItemDto
import com.po4yka.bitesizereader.data.remote.dto.SyncApplyRequestDto
import com.po4yka.bitesizereader.data.remote.dto.SyncSessionRequestDto
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.domain.model.SyncConflict
import com.po4yka.bitesizereader.domain.model.SyncResult
import com.po4yka.bitesizereader.domain.model.SyncState
import com.po4yka.bitesizereader.domain.repository.ApplyResult
import com.po4yka.bitesizereader.domain.repository.LocalChange
import com.po4yka.bitesizereader.domain.repository.SyncRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.time.Clock
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single
class SyncRepositoryImpl(
    private val database: Database,
    private val api: SyncApi,
    private val secureStorage: SecureStorage,
) : SyncRepository {
    // ========================================================================
    // Legacy Sync (backward compatibility)
    // ========================================================================

    override suspend fun sync(forceFull: Boolean) {
        val sessionId = secureStorage.getSessionId()
        if (sessionId == null) {
            logger.warn { "Cannot sync: No session ID found (secret login does not support sync)" }
            throw IllegalStateException(
                "Sync requires a session ID. " +
                    "Developer secret login does not support sync - please use Telegram login.",
            )
        }
        logger.info { "Starting sync with sessionId=$sessionId, forceFull=$forceFull" }

        val metadata = database.databaseQueries.getSyncMetadata().executeAsOneOrNull()
        val sinceEpochSeconds = if (forceFull) 0L else (metadata?.lastSyncTime?.epochSeconds ?: 0L)

        val response = api.sync(sessionId, sinceEpochSeconds)

        if (!response.success || response.data == null) {
            logger.error { "Sync failed or empty response: ${response.error}" }
            throw IllegalStateException(response.error?.message ?: "Sync failed")
        }

        val changes = response.data.changes
        val syncTimestamp = response.data.syncTimestamp

        logger.info {
            "Sync success. Created: ${changes.created.size}, " +
                "Updated: ${changes.updated.size}, Deleted: ${changes.deleted.size}"
        }

        database.transaction {
            changes.created.forEach { created ->
                val payload = created.data
                database.databaseQueries.insertSummary(
                    payload.toEntity(isReadOverride = payload.isRead, createdAt = created.createdAt),
                )
            }
            changes.updated.forEach { updated ->
                val payload = updated.data
                database.databaseQueries.insertSummary(
                    payload.toEntity(isReadOverride = payload.isRead, createdAt = updated.createdAt),
                )
            }
            changes.deleted.forEach { id ->
                database.databaseQueries.deleteSummary(id.toString())
            }

            database.databaseQueries.updateSyncMetadata(
                lastSyncTime = Clock.System.now(),
                syncToken = syncTimestamp,
            )
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
    ): SyncResult {
        logger.info { "Starting full sync with sessionId=$sessionId, limit=$limit" }
        val response = api.fullSync(sessionId, limit)

        if (!response.success || response.data == null) {
            logger.error { "Full sync failed: ${response.error}" }
            throw IllegalStateException(response.error?.message ?: "Full sync failed")
        }

        val data = response.data
        logger.info { "Full sync received ${data.items.size} items, hasMore=${data.hasMore}" }

        // Process items and store in database
        database.transaction {
            data.items.forEach { item ->
                when (item.entityType) {
                    "summary" -> processSyncSummaryItem(item.id, item.payload)
                    else -> logger.warn { "Unknown entity type: ${item.entityType}" }
                }
            }
        }

        return SyncResult(
            createdCount = data.items.size,
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

        // Process changes and store in database
        database.transaction {
            data.created.forEach { item ->
                when (item.entityType) {
                    "summary" -> processSyncSummaryItem(item.id, item.payload)
                    else -> logger.warn { "Unknown entity type: ${item.entityType}" }
                }
            }
            data.updated.forEach { item ->
                when (item.entityType) {
                    "summary" -> processSyncSummaryItem(item.id, item.payload)
                    else -> logger.warn { "Unknown entity type: ${item.entityType}" }
                }
            }
            data.deleted.forEach { id ->
                database.databaseQueries.deleteSummary(id.toString())
            }

            // Update sync metadata
            database.databaseQueries.updateSyncMetadata(
                lastSyncTime = Clock.System.now(),
                syncToken = data.newCursor?.toString() ?: "",
            )
        }

        return SyncResult(
            createdCount = data.created.size,
            updatedCount = data.updated.size,
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

    private fun processSyncSummaryItem(
        id: Long,
        payload: JsonObject?,
    ) {
        if (payload == null) {
            logger.warn { "Sync item $id has no payload, skipping" }
            return
        }
        // Extract fields from JSON payload and upsert to database
        // This is a simplified implementation - full implementation would parse all fields
        logger.debug { "Processing sync summary item: $id" }
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
