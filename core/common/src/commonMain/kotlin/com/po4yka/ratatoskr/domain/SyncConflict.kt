package com.po4yka.ratatoskr.domain.model

/**
 * Represents a conflict detected during sync apply.
 */
data class SyncConflict(
    val id: Long,
    val entityType: String,
    val clientVersion: Long,
    val serverVersion: Long,
    val reason: String,
)
