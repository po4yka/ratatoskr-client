package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.database.PendingOperationEntity
import com.po4yka.bitesizereader.feature.sync.api.PendingOperationHandler
import com.po4yka.bitesizereader.feature.sync.api.PendingOperationHandlingResult
import com.po4yka.bitesizereader.feature.sync.domain.repository.LocalChange
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class PendingOperationRoutingTest {
    @Test
    fun `routes operations through registered handlers only`() =
        runTest {
            val expectedChange =
                LocalChange(
                    entityType = "summary",
                    id = 1,
                    action = "update",
                    lastSeenVersion = 7,
                    payload = mapOf("title" to "Updated"),
                )
            val operations =
                listOf(
                    PendingOperationEntity(
                        id = 1,
                        entityId = "summary-1",
                        entityType = "summary",
                        action = "update",
                        payload = """{"title":"Updated"}""",
                        createdAt = 10,
                    ),
                    PendingOperationEntity(
                        id = 2,
                        entityId = "highlight-1",
                        entityType = "highlight",
                        action = "delete",
                        payload = null,
                        createdAt = 20,
                    ),
                    PendingOperationEntity(
                        id = 3,
                        entityId = "unknown-1",
                        entityType = "unknown",
                        action = "create",
                        payload = null,
                        createdAt = 30,
                    ),
                )

            val result =
                routePendingOperations(
                    pendingOps = operations,
                    handlers =
                        listOf(
                            StaticPendingOperationHandler(
                                entityType = "summary",
                                result =
                                    PendingOperationHandlingResult.QueueChange(
                                        expectedChange,
                                    ),
                            ),
                            StaticPendingOperationHandler(
                                entityType = "highlight",
                                result = PendingOperationHandlingResult.Completed(conflictCount = 1),
                            ),
                        ),
                )

            assertEquals(listOf(1L to expectedChange), result.queuedChanges)
            assertEquals(listOf(2L), result.completedOperationIds)
            assertEquals(1, result.conflictCount)
        }

    @Test
    fun `retry later operations stay queued`() =
        runTest {
            val operation =
                PendingOperationEntity(
                    id = 11,
                    entityId = "highlight-2",
                    entityType = "highlight",
                    action = "update",
                    payload = """{"note":"retry"}""",
                    createdAt = 15,
                )

            val result =
                routePendingOperations(
                    pendingOps = listOf(operation),
                    handlers =
                        listOf(
                            StaticPendingOperationHandler(
                                entityType = "highlight",
                                result = PendingOperationHandlingResult.RetryLater,
                            ),
                        ),
                )

            assertEquals(emptyList(), result.queuedChanges)
            assertEquals(emptyList(), result.completedOperationIds)
            assertEquals(0, result.conflictCount)
        }

    private class StaticPendingOperationHandler(
        private val entityType: String,
        private val result: PendingOperationHandlingResult,
    ) : PendingOperationHandler {
        override fun canHandle(operation: PendingOperationEntity): Boolean = operation.entityType == entityType

        override suspend fun handle(operation: PendingOperationEntity): PendingOperationHandlingResult = result
    }
}
