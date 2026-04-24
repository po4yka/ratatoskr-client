package com.po4yka.bitesizereader.data.repository

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FullSyncCleanupDecisionTest {
    @Test
    fun `allows stale cleanup after full dataset is observed from start`() {
        assertTrue(
            shouldCleanupStaleSummariesAfterFullSync(
                resumeCursor = null,
                observedCompleteDataset = true,
            ),
        )
    }

    @Test
    fun `skips stale cleanup after resumed full sync`() {
        assertFalse(
            shouldCleanupStaleSummariesAfterFullSync(
                resumeCursor = 100,
                observedCompleteDataset = true,
            ),
        )
    }

    @Test
    fun `skips stale cleanup when pagination stops before complete dataset`() {
        assertFalse(
            shouldCleanupStaleSummariesAfterFullSync(
                resumeCursor = null,
                observedCompleteDataset = false,
            ),
        )
    }

    @Test
    fun `checkpoint token prefers next cursor then server version then fallback`() {
        assertEquals("42", syncCheckpointToken(nextCursor = 42, serverVersion = 100, fallbackToken = "7"))
        assertEquals("100", syncCheckpointToken(nextCursor = null, serverVersion = 100, fallbackToken = "7"))
        assertEquals("7", syncCheckpointToken(nextCursor = null, serverVersion = null, fallbackToken = "7"))
    }
}
