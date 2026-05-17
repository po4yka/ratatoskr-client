package com.po4yka.ratatoskr.feature.sync.api

import com.po4yka.ratatoskr.database.PendingOperationEntity
import com.po4yka.ratatoskr.feature.sync.domain.repository.LocalChange

interface SyncItemApplier {
    val entityType: String

    fun apply(entity: SyncEntity): Boolean
}

interface PendingOperationHandler {
    fun canHandle(operation: PendingOperationEntity): Boolean

    suspend fun handle(operation: PendingOperationEntity): PendingOperationHandlingResult
}

sealed interface PendingOperationHandlingResult {
    data class QueueChange(
        val change: LocalChange,
    ) : PendingOperationHandlingResult

    data class Completed(
        val conflictCount: Int = 0,
    ) : PendingOperationHandlingResult

    data object RetryLater : PendingOperationHandlingResult
}
