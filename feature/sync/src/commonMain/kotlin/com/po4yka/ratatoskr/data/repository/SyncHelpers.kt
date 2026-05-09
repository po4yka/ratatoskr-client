package com.po4yka.ratatoskr.data.repository

internal fun shouldCleanupStaleSummariesAfterFullSync(
    resumeCursor: Long?,
    observedCompleteDataset: Boolean,
): Boolean = resumeCursor == null && observedCompleteDataset

internal fun syncCheckpointToken(
    nextCursor: Long?,
    serverVersion: Long?,
    fallbackToken: String?,
): String? = (nextCursor ?: serverVersion)?.toString() ?: fallbackToken
