package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.database.Database
import com.po4yka.ratatoskr.database.PendingOperationEntity
import com.po4yka.ratatoskr.feature.sync.api.PendingOperationHandler
import com.po4yka.ratatoskr.feature.sync.api.PendingOperationHandlingResult
import com.po4yka.ratatoskr.feature.sync.domain.repository.ApplyResult
import com.po4yka.ratatoskr.feature.sync.domain.repository.LocalChange
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CancellationException

private val logger = KotlinLogging.logger {}

internal data class PendingOperationRoutingResult(
    val queuedChanges: List<Pair<Long, LocalChange>>,
    val completedOperationIds: List<Long>,
    val conflictCount: Int,
)

internal suspend fun routePendingOperations(
    pendingOps: List<PendingOperationEntity>,
    handlers: List<PendingOperationHandler>,
): PendingOperationRoutingResult {
    val queuedChanges = mutableListOf<Pair<Long, LocalChange>>()
    val completedOperationIds = mutableListOf<Long>()
    var conflictCount = 0

    pendingOps.forEach { operation ->
        val handler = handlers.firstOrNull { it.canHandle(operation) }
        if (handler == null) {
            logger.warn {
                "No pending operation handler registered for ${operation.entityType}:${operation.action}"
            }
            return@forEach
        }

        when (val result = handler.handle(operation)) {
            is PendingOperationHandlingResult.QueueChange -> {
                queuedChanges += operation.id to result.change
            }
            is PendingOperationHandlingResult.Completed -> {
                completedOperationIds += operation.id
                conflictCount += result.conflictCount
            }
            PendingOperationHandlingResult.RetryLater -> {
                logger.debug {
                    "Keeping pending operation ${operation.id} (${operation.entityType}:${operation.action}) for retry"
                }
            }
        }
    }

    return PendingOperationRoutingResult(
        queuedChanges = queuedChanges,
        completedOperationIds = completedOperationIds,
        conflictCount = conflictCount,
    )
}

internal class PendingOperationFlusher(
    private val database: Database,
    handlers: List<PendingOperationHandler>,
    private val applyChanges: suspend (sessionId: String, changes: List<LocalChange>) -> ApplyResult,
    private val onConflictCount: (Int) -> Unit,
) {
    private val handlers = handlers

    suspend fun flush(sessionId: String) {
        val pendingOps = database.databaseQueries.selectAllPendingOperations().executeAsList()
        if (pendingOps.isEmpty()) return

        logger.info { "Flushing ${pendingOps.size} pending operations" }

        val routingResult = routePendingOperations(pendingOps = pendingOps, handlers = handlers)
        routingResult.completedOperationIds.forEach { operationId ->
            database.databaseQueries.deletePendingOperation(operationId)
        }
        if (routingResult.conflictCount > 0) {
            onConflictCount(routingResult.conflictCount)
        }

        val changes = routingResult.queuedChanges.map { it.second }
        if (changes.isEmpty()) return

        try {
            val result = applyChanges(sessionId, changes)
            routingResult.queuedChanges.forEach { (id, _) ->
                database.databaseQueries.deletePendingOperation(id)
            }
            database.databaseQueries.deleteAllPendingDeletes()
            if (result.conflicts.isNotEmpty()) {
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
                onConflictCount(result.conflicts.size)
            }
            logger.info {
                "Successfully flushed ${changes.size} pending operations " +
                    "(applied=${result.appliedCount}, conflicts=${result.conflicts.size})"
            }
        } catch (e: CancellationException) {
            throw e
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.warn(e) { "Failed to flush pending operations, aborting sync so local changes remain authoritative" }
            throw e
        }
    }
}
